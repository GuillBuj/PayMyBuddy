package com.paymybuddy.paymybuddy.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.paymybuddy.paymybuddy.dto.BuddyConnectionDTO;
import com.paymybuddy.paymybuddy.dto.RegisterUserDTO;
import com.paymybuddy.paymybuddy.dto.UpdateUserDTO;
import com.paymybuddy.paymybuddy.exception.BuddyAlreadyAddedException;
import com.paymybuddy.paymybuddy.exception.BuddyNotFoundException;
import com.paymybuddy.paymybuddy.exception.EmailAlreadyUsedException;
import com.paymybuddy.paymybuddy.exception.NotFoundException;
import com.paymybuddy.paymybuddy.exception.SelfAddException;
import com.paymybuddy.paymybuddy.exception.UsernameAlreadyTakenException;
import com.paymybuddy.paymybuddy.model.User;
import com.paymybuddy.paymybuddy.repository.TransactionRepository;
import com.paymybuddy.paymybuddy.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    
    @Mock
    private UserRepository userRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private BalanceService operationService;

    @InjectMocks
    private UserService userService;

    private User user;

    @BeforeEach
    void setUp(){
        user = new User(1, "rory", "rory@gmail.com", "encodedPassword", BigDecimal.valueOf(100.00), new HashSet<>(), LocalDateTime.now());
    }

    @Test
    void createUserOkTest() {
        
        RegisterUserDTO userDTO = new RegisterUserDTO("jimi", "jimi@gmail.com", "password123");

        when(userRepository.existsByUsername("jimi")).thenReturn(false);
        when(userRepository.existsByEmail("jimi@gmail.com")).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        
        User mockUser = new User();
        mockUser.setUsername("jimi");
        mockUser.setEmail("jimi@gmail.com");
        mockUser.setPassword("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(mockUser);

        User createdUser = userService.createUser(userDTO);

        assertNotNull(createdUser);
        assertEquals("jimi", createdUser.getUsername());
        assertEquals("jimi@gmail.com", createdUser.getEmail());
        assertEquals("encodedPassword", createdUser.getPassword());
        
        verify(userRepository).save(any(User.class));
    }

    @Test
    void createUserUsernameAlreadyTakenTest() {
        when(userRepository.existsByUsername("rory")).thenReturn(true);

        UsernameAlreadyTakenException thrown = assertThrows(UsernameAlreadyTakenException.class, () -> {
            userService.createUser(new RegisterUserDTO("rory", "roryg@gmail.com", "password123"));
        });

        assertEquals("Username already taken: rory", thrown.getMessage());

        verify(userRepository, never()).save(any(User.class));
    }
    
    @Test
    void createUserEmailAlreadyUsedTest() {
        when(userRepository.existsByUsername("roryg")).thenReturn(false);
        when(userRepository.existsByEmail("rory@gmail.com")).thenReturn(true);

        EmailAlreadyUsedException thrown = assertThrows(EmailAlreadyUsedException.class, () -> {
            userService.createUser(new RegisterUserDTO("roryg", "rory@gmail.com", "password123"));
        });

        assertEquals("Email already used: rory@gmail.com", thrown.getMessage());

        verify(userRepository, never()).save(any(User.class));
    }


    @Test
    void updateUserOkTest() {
        
        UpdateUserDTO userDTO = new UpdateUserDTO(1, "badpenny", "roryg@gmail.com", "newPassword123");

        when(userRepository.getById(1)).thenReturn(user);
        when(userRepository.existsByUsername("badpenny")).thenReturn(false);
        when(userRepository.existsByEmail("roryg@gmail.com")).thenReturn(false);
        when(passwordEncoder.encode("newPassword123")).thenReturn("newEncodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);

        User updatedUser = userService.updateUser(userDTO);

        assertNotNull(updatedUser);
        assertEquals("badpenny", updatedUser.getUsername());
        assertEquals("roryg@gmail.com", updatedUser.getEmail());
        assertEquals("newEncodedPassword", updatedUser.getPassword());

        verify(userRepository).save(any(User.class));
    }

    @Test
    void updateUserUsernameAlreadyTakenTest() {
        when(userRepository.getById(1)).thenReturn(user);
        when(userRepository.existsByUsername("existingusername")).thenReturn(true);

        UsernameAlreadyTakenException thrown = assertThrows(UsernameAlreadyTakenException.class, () -> {
            userService.updateUser(new UpdateUserDTO(1, "existingUsername", "rory@gmail.com", ""));
        });

        assertEquals("Username already taken: existingusername", thrown.getMessage());

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void updateUserEmailAlreadyUsedTest() {
        when(userRepository.getById(1)).thenReturn(user);
        when(userRepository.existsByEmail("usedemail@mail.com")).thenReturn(true);

        EmailAlreadyUsedException thrown = assertThrows(EmailAlreadyUsedException.class, () -> {
            userService.updateUser(new UpdateUserDTO(1, "rory", "usedEmail@mail.com", ""));
        });
        
        assertEquals("Email already used: usedemail@mail.com", thrown.getMessage());

        verify(userRepository, never()).save(any(User.class));    
    }

    @Test
    void addBuddyOkTest() {
        
        User buddy = new User(2, "stevie", "stevie@gmail.com", "encodedPassword", BigDecimal.valueOf(100.00), new HashSet<>(), LocalDateTime.now());
        BuddyConnectionDTO buddyConnection = new BuddyConnectionDTO("rory@example.com", "stevie@example.com");

        when(userRepository.findByEmail("rory@example.com")).thenReturn(Optional.of(user));
        when(userRepository.findByEmail("stevie@example.com")).thenReturn(Optional.of(buddy));
        when(userRepository.save(user)).thenReturn(user);

        userService.addBuddy(buddyConnection);

        assertTrue(user.getBuddies().contains(buddy));

        verify(userRepository, times(1)).findByEmail("rory@example.com");
        verify(userRepository, times(1)).findByEmail("stevie@example.com");
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void addBuddySelfAddTest() {
        
        BuddyConnectionDTO buddyConnection = new BuddyConnectionDTO("rory@example.com", "rory@example.com");

        assertThrows(SelfAddException.class, ()-> userService.addBuddy(buddyConnection));

        assertTrue(!user.getBuddies().contains(user));
        verify(userRepository, never()).save(user);
    }

    @Test
    void addBuddyBuddyAlreadyAddedTest() {
        User buddy = new User(2, "stevie", "stevie@gmail.com", "encodedPassword", BigDecimal.valueOf(100.00), new HashSet<>(), LocalDateTime.now());
        BuddyConnectionDTO buddyConnection = new BuddyConnectionDTO("rory@example.com", "stevie@gmail.com");

        user.getBuddies().add(buddy);

        when(userRepository.findByEmail("rory@example.com")).thenReturn(Optional.of(user));
        when(userRepository.findByEmail("stevie@gmail.com")).thenReturn(Optional.of(buddy));

        assertThrows(BuddyAlreadyAddedException.class, () -> userService.addBuddy(buddyConnection));

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void addBuddyBuddyNotFoundTest() {
        BuddyConnectionDTO buddyConnection = new BuddyConnectionDTO("rory@example.com", "notexisting@gmail.com");

        when(userRepository.findByEmail("rory@example.com")).thenReturn(Optional.of(user));
        when(userRepository.findByEmail("notexisting@gmail.com")).thenReturn(Optional.empty());

        assertThrows(BuddyNotFoundException.class, () -> userService.addBuddy(buddyConnection));

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void addBuddyUserNotFoundTest() {
        BuddyConnectionDTO buddyConnection = new BuddyConnectionDTO("notexisting@example.com", "rory@gmail.com");

        when(userRepository.findByEmail("notexisting@example.com")).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.addBuddy(buddyConnection));

        verify(userRepository, never()).save(any(User.class));
    }
}