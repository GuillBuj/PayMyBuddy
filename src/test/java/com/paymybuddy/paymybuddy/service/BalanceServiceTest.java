package com.paymybuddy.paymybuddy.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.paymybuddy.paymybuddy.dto.BalanceOperationDTO;
import com.paymybuddy.paymybuddy.exception.NotFoundException;
import com.paymybuddy.paymybuddy.model.User;
import com.paymybuddy.paymybuddy.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
public class BalanceServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private BalanceService balanceService;

    private User user;

    @BeforeEach
    void setUp(){
        user = new User(1, "rory", "rory@gmail.com", "123", new BigDecimal(100.00), null, LocalDateTime.now());
    }

    @Test
    void updateBalancePlus(){
        
        BalanceOperationDTO operation = new BalanceOperationDTO("rory@gmail.com", new BigDecimal(50.00));
        when(userRepository.findByEmail("rory@gmail.com")).thenReturn(Optional.of(user));

        balanceService.updateBalance(operation, true);

        assertEquals(new BigDecimal(150.00), user.getBalance());
        verify(userRepository).save(user);
    }

    @Test
    void updateBalanceMinus(){
        
        BalanceOperationDTO operation = new BalanceOperationDTO("rory@gmail.com", new BigDecimal(50.00));
        when(userRepository.findByEmail("rory@gmail.com")).thenReturn(Optional.of(user));

        balanceService.updateBalance(operation, false);

        assertEquals(new BigDecimal(50.00), user.getBalance());
        verify(userRepository).save(user);
    }

    @Test
    void updateBalanceNotFound(){
        
        BalanceOperationDTO operation = new BalanceOperationDTO("unknown@gmail.com", new BigDecimal(50.00));
        when(userRepository.findByEmail("unknown@gmail.com")).thenReturn(Optional.empty());

        NotFoundException e = assertThrows(
            NotFoundException.class,
            () -> balanceService.updateBalance(operation, true)
        );

        assertEquals("User not found:unknown@gmail.com", e.getMessage());

        verify(userRepository, never()).save(any(User.class));
    }
}
