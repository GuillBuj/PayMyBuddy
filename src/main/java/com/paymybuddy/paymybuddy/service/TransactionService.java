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

@Service
@AllArgsConstructor
@Slf4j
public class TransactionService {
    
    private final TransactionRepository transactionRepository;

    private final UserRepository userRepository;

    private BalanceService operationService;

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

    public void addToBalance(BalanceOperationDTO operation){
        log.debug("*** Adding to balance");

        operationService.updateBalance(operation, true);
    }

    public void subtractFromBalance(BalanceOperationDTO operation){
        log.debug("*** Subtracting from balance");

        operationService.updateBalance(operation, false);
    }

    public void validateEnoughMoney(BalanceOperationDTO operation){
        log.debug("*** Validating enough money for transaction: ", operation);

        User user = userRepository.findByEmail(operation.userEmail())
                .orElseThrow(()-> new NotFoundException("User not found:" + operation.userEmail()));

        if(user.getBalance().compareTo(operation.amount())<0){
            throw new NotEnoughMoneyException("*** Not enough money for this transaction: " + operation);
        }
    }

    public void validateNotSelfSend(TransactionRequestDTO transactionDTO){
        log.debug("*** Validating transaction is not self send: ", transactionDTO);

        if(transactionDTO.receiverEmail().equals(transactionDTO.senderEmail())){
            throw new SelfSendException("Self send transaction");
        }
    }

    public void validatePositiveTransaction(TransactionRequestDTO transactionDTO){
        log.debug("*** Validating amount is positive: ", transactionDTO);

        if(transactionDTO.amount().compareTo(BigDecimal.ZERO) <= 0){
            throw new NegativeTransactionException("Negative amount transaction");
        }
    }
}
