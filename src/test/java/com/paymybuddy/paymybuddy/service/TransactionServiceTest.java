package com.paymybuddy.paymybuddy.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.paymybuddy.paymybuddy.dto.BalanceOperationDTO;
import com.paymybuddy.paymybuddy.dto.TransactionRequestDTO;
import com.paymybuddy.paymybuddy.exception.NotEnoughMoneyException;
import com.paymybuddy.paymybuddy.model.Transaction;
import com.paymybuddy.paymybuddy.model.User;
import com.paymybuddy.paymybuddy.repository.TransactionRepository;
import com.paymybuddy.paymybuddy.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
public class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BalanceService balanceService;

    @InjectMocks
    private TransactionService transactionService;

    private User sender;
    private User receiver;
    private TransactionRequestDTO transactionDTO;

    @BeforeEach
    void setUp(){
        sender = new User(1, "rory", "rory@gmail.com", "123", new BigDecimal(100.00), null, LocalDateTime.now());
        receiver = new User(2, "jimi", "jimi@gmail.com", "123", new BigDecimal(100.00), null, LocalDateTime.now());
        transactionDTO = new TransactionRequestDTO("rory@gmail.com","jimi@gmail.com", new BigDecimal(50.00), "Test");
    }

    @Test
    void createTransactionOkTest(){
        when(userRepository.findByEmail("rory@gmail.com")).thenReturn(Optional.of(sender));
        when(userRepository.findByEmail("jimi@gmail.com")).thenReturn(Optional.of(receiver));
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Transaction result = transactionService.createTransaction(transactionDTO);

        assertNotNull(result);
        assertEquals(sender, result.getSender());
        assertEquals(receiver, result.getReceiver());
        assertEquals(BigDecimal.valueOf(50), result.getAmount());
        assertEquals("Test", result.getDescription());
        assertNotNull(result.getDateCreated());

        verify(transactionRepository, times(1)).save(any(Transaction.class));
    }

    @Test
    void validateEnoughMoneyOkTest() {
        
        when(userRepository.findByEmail("rory@example.com")).thenReturn(Optional.of(sender));

        assertDoesNotThrow(() -> {
            transactionService.validateEnoughMoney(new BalanceOperationDTO("rory@example.com", BigDecimal.valueOf(1)));
        });
    }

    @Test
    void validateEnoughMoneyNokTest() {
    
        when(userRepository.findByEmail("rory@example.com")).thenReturn(Optional.of(sender));

        NotEnoughMoneyException exception = assertThrows(NotEnoughMoneyException.class, () -> {
            transactionService.validateEnoughMoney(new BalanceOperationDTO("rory@example.com", BigDecimal.valueOf(1000)));
        });

        assertEquals("Not enough money for this transaction", exception.getMessage());
    }
    
}
