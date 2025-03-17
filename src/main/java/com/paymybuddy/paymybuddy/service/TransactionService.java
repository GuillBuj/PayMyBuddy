package com.paymybuddy.paymybuddy.service;


import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.paymybuddy.paymybuddy.dto.BalanceOperationDTO;
import com.paymybuddy.paymybuddy.dto.TransactionRequestDTO;
import com.paymybuddy.paymybuddy.exception.NegativeTransactionException;
import com.paymybuddy.paymybuddy.exception.NotEnoughMoneyException;
import com.paymybuddy.paymybuddy.exception.NotFoundException;
import com.paymybuddy.paymybuddy.exception.SelfSendException;
import com.paymybuddy.paymybuddy.model.Transaction;
import com.paymybuddy.paymybuddy.model.User;
import com.paymybuddy.paymybuddy.repository.TransactionRepository;
import com.paymybuddy.paymybuddy.repository.UserRepository;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * The `TransactionService` class provides services for creating and managing transactions
 * between users, including validating transaction details, checking balances, and updating
 * user accounts. It uses the `TransactionRepository` and `UserRepository` for persistence
 * and a `BalanceService` for updating the balances of users involved in a transaction.
 */
@Service
@AllArgsConstructor
@Slf4j
public class TransactionService {
    
    private final TransactionRepository transactionRepository;

    private final UserRepository userRepository;

    private BalanceService operationService;

    /**
     * Creates a new transaction based on the information provided in the TransactionRequestDTO.
     * This method performs validations, checks if the sender has enough funds, updates their balances,
     * and saves the transaction.
     * 
     * @param transactionDTO The transaction details, including sender and receiver emails, amount, and description.
     * @return The created {@link Transaction} object after being saved to the database.
     * @throws NotFoundException If the sender or receiver user is not found.
     * @throws NotEnoughMoneyException If the sender does not have enough money for the transaction.
     * @throws SelfSendException If the sender tries to send money to themselves.
     * @throws NegativeTransactionException If the transaction amount is zero or negative.
     */
    @Transactional
    public Transaction createTransaction(TransactionRequestDTO transactionDTO){
        log.debug("*** Creating transaction: {}", transactionDTO);
      
        // pas possible en utilisation normale
        validateNotSelfSend(transactionDTO);
        validatePositiveTransaction(transactionDTO);
        
        User sender = userRepository.findByEmail(transactionDTO.senderEmail())
                .orElseThrow(()-> new NotFoundException("User not found:" + transactionDTO.senderEmail()));
                
        User receiver = userRepository.findByEmail(transactionDTO.receiverEmail())
                .orElseThrow(()-> new NotFoundException("Buddy not found:" + transactionDTO.receiverEmail()));

        BigDecimal amount = transactionDTO.amount();
        
        BalanceOperationDTO operation = new BalanceOperationDTO(transactionDTO.senderEmail(),amount);
        
        validateEnoughMoney(operation);

        subtractFromBalance(operation);
        addToBalance(new BalanceOperationDTO(transactionDTO.receiverEmail(), amount));

        Transaction transaction = new Transaction();
        transaction.setSender(sender);
        transaction.setReceiver(receiver);
        transaction.setAmount(amount);
        transaction.setDescription(transactionDTO.description());
        transaction.setFee(BigDecimal.ZERO); // pour l'instant à zéro
        transaction.setDateCreated(LocalDateTime.now());

        Transaction transactionDone = transactionRepository.save(transaction);
        log.info("*** Transaction saved: {}", transactionDone);

        return transactionDone;
    }

    /**
     * Adds the specified amount to the user's balance.
     * 
     * @param operation The balance operation containing the user's email and the amount to be added.
     */
    public void addToBalance(BalanceOperationDTO operation){
        log.debug("*** Adding to balance");

        operationService.updateBalance(operation, true);
    }

    /**
     * Subtracts the specified amount from the user's balance.
     * 
     * @param operation The balance operation containing the user's email and the amount to be subtracted.
     */
    public void subtractFromBalance(BalanceOperationDTO operation){
        log.debug("*** Subtracting from balance");

        operationService.updateBalance(operation, false);
    }

    /**
     * Validates whether the user has enough money for a transaction.
     * 
     * @param operation The balance operation containing the user's email and the transaction amount.
     * @throws NotEnoughMoneyException If the user does not have enough money to complete the transaction.
     */
    public void validateEnoughMoney(BalanceOperationDTO operation){
        log.debug("*** Validating enough money for transaction: ", operation);

        User user = userRepository.findByEmail(operation.userEmail())
                .orElseThrow(()-> new NotFoundException("User not found:" + operation.userEmail()));

        if(user.getBalance().compareTo(operation.amount())<0){
            throw new NotEnoughMoneyException("*** Not enough money for this transaction: " + operation);
        }
    }

    /**
     * Validates that the sender is not trying to send money to himself.
     * 
     * @param transactionDTO The transaction details, including the sender and receiver emails.
     * @throws SelfSendException If the sender and receiver emails are the same.
     */
    public void validateNotSelfSend(TransactionRequestDTO transactionDTO){
        log.debug("*** Validating transaction is not self send: ", transactionDTO);

        if(transactionDTO.receiverEmail().equals(transactionDTO.senderEmail())){
            throw new SelfSendException("Self send transaction");
        }
    }

    /**
     * Validates that the transaction amount is positive (greater than zero).
     * 
     * @param transactionDTO The transaction details, including the amount to be validated.
     * @throws NegativeTransactionException If the transaction amount is zero or negative.
     */
    public void validatePositiveTransaction(TransactionRequestDTO transactionDTO){
        log.debug("*** Validating amount is positive: ", transactionDTO);

        if(transactionDTO.amount().compareTo(BigDecimal.ZERO) <= 0){
            throw new NegativeTransactionException("Negative amount transaction");
        }
    }
}
