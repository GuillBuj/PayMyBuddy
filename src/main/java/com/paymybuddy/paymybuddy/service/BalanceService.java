package com.paymybuddy.paymybuddy.service;

import org.springframework.stereotype.Service;

import com.paymybuddy.paymybuddy.dto.BalanceOperationDTO;
import com.paymybuddy.paymybuddy.exception.NotFoundException;
import com.paymybuddy.paymybuddy.model.User;
import com.paymybuddy.paymybuddy.repository.UserRepository;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * The `BalanceService` class is responsible for handling balance operations for users.
 * It interacts with the `UserRepository` to retrieve and update the user's balance
 * based on the balance operation provided.
 * 
 * This service includes methods for updating a user's balance by either adding or
 * subtracting an amount based on the operation performed.
 */
@Service
@AllArgsConstructor
@Slf4j
public class BalanceService {

    UserRepository userRepository;

    /**
     * The function `updateBalance` takes a `BalanceOperationDTO` object and a boolean flag to either
     * add or subtract the amount from the user's balance, updating and saving the user's balance
     * accordingly.
     * 
     * @param operation The `operation` parameter is of type `BalanceOperationDTO`, which likely
     * contains information about a balance operation such as the user email and the amount to be added
     * or subtracted from the balance.
     * @param plus The `plus` parameter is a boolean value that determines whether to add or subtract
     * the `amount` from the user's balance. If `plus` is true, the `amount` will be added to the
     * balance. If `plus` is false, the `amount` will be subtracted from
     */
    public void updateBalance(BalanceOperationDTO operation, boolean plus){
        log.debug("*** Updating balance: {}, {}", operation, plus);

        User user = userRepository.findByEmail(operation.userEmail())
            .orElseThrow(()-> new NotFoundException("User not found:" + operation.userEmail()));

        if(plus){
            user.setBalance(user.getBalance().add(operation.amount()));
        } else {
            user.setBalance(user.getBalance().subtract(operation.amount()));
        }

        log.info("*** Balance updated: {}, {}.", operation, plus);

        userRepository.save(user);
    }
}
