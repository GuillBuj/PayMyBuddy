package com.paymybuddy.paymybuddy.service;


import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.paymybuddy.paymybuddy.dto.BalanceOperationDTO;
import com.paymybuddy.paymybuddy.dto.TransactionRequestDTO;
import com.paymybuddy.paymybuddy.exception.NotEnoughMoneyException;
import com.paymybuddy.paymybuddy.exception.NotFoundException;
import com.paymybuddy.paymybuddy.model.Transaction;
import com.paymybuddy.paymybuddy.model.User;
import com.paymybuddy.paymybuddy.repository.TransactionRepository;
import com.paymybuddy.paymybuddy.repository.UserRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class TransactionService {
    
    private final TransactionRepository transactionRepository;

    private final UserRepository userRepository;

    private UserService userService;

    @Transactional
    public Transaction createTransaction(TransactionRequestDTO transaction){
        User sender = userRepository.findByEmail(transaction.senderEmail())
                .orElseThrow(()-> new NotFoundException("User not found with email " + transaction.senderEmail()));
                
        User receiver = userRepository.findByEmail(transaction.receiverEmail())
                .orElseThrow(()-> new NotFoundException("Buddy not found with email " + transaction.receiverEmail()));

        BigDecimal amount = transaction.amount();
        
        BalanceOperationDTO operation = new BalanceOperationDTO(transaction.senderEmail(),amount);
        
        userService.validateEnoughMoney(operation);

        userService.subtractFromBalance(operation);
        userService.addToBalance(new BalanceOperationDTO(transaction.receiverEmail(), amount));

        Transaction transactionDone = new Transaction();
        transactionDone.setSender(sender);
        transactionDone.setReceiver(receiver);
        transactionDone.setAmount(amount);
        transactionDone.setDescription(transaction.description());
        //pour l'instant à zéro
        transactionDone.setFee(BigDecimal.ZERO);
        transactionDone.setDateCreated(LocalDateTime.now());


        return transactionRepository.save(transactionDone);
    }
}
