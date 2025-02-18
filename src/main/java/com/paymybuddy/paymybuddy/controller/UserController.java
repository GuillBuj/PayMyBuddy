package com.paymybuddy.paymybuddy.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.paymybuddy.paymybuddy.dto.BalanceOperationDTO;
import com.paymybuddy.paymybuddy.dto.TransactionInList;
import com.paymybuddy.paymybuddy.dto.TransactionListDTO;
import com.paymybuddy.paymybuddy.dto.TransactionRequestDTO;
import com.paymybuddy.paymybuddy.dto.UserDTO;
import com.paymybuddy.paymybuddy.model.Transaction;
import com.paymybuddy.paymybuddy.service.TransactionService;
import com.paymybuddy.paymybuddy.service.UserService;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/user")
@AllArgsConstructor
public class UserController {

    @Autowired
    private final UserService userService;

    @Autowired
    private final TransactionService transactionService;

    @GetMapping(produces = "application/json")
    public ResponseEntity<UserDTO> getUserById(@RequestParam int id) {
        UserDTO userDTO = userService.getUserById(id);
        return ResponseEntity.ok(userDTO);
    }

    @PostMapping("/pay")
    public ResponseEntity<Transaction> sendMoney(@RequestBody TransactionRequestDTO transactionDTO){
        Transaction transaction = transactionService.createTransaction(transactionDTO);
        return ResponseEntity.ok(transaction);
    }

    //pour test transaction
    @PostMapping("/deposit")
    public ResponseEntity<String> deposit(@RequestBody BalanceOperationDTO operation){
        userService.deposit(operation);
        return ResponseEntity.ok("Money added");
    }

    @GetMapping("/transactions")
    public TransactionListDTO getTransactions(@RequestParam int id){
        return userService.getTransactions(id);
    }
}
