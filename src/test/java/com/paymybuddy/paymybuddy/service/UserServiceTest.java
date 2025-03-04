package com.paymybuddy.paymybuddy.service;

<<<<<<< HEAD
public class UserServiceTest {

=======
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.paymybuddy.paymybuddy.dto.RegisterUserDTO;
import com.paymybuddy.paymybuddy.dto.UpdateUserDTO;
import com.paymybuddy.paymybuddy.exception.AlreadyExistsException;
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
        user = new User(1, "rory", "rory@gmail.com", "encodedPassword", BigDecimal.valueOf(100.00), null, LocalDateTime.now());
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

        AlreadyExistsException thrown = assertThrows(AlreadyExistsException.class, () -> {
            userService.createUser(new RegisterUserDTO("rory", "roryg@gmail.com", "password123"));
        });

        assertEquals("Username already taken: rory", thrown.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void createUserEmailAlreadyUsedTest() {
        
        when(userRepository.existsByUsername("roryg")).thenReturn(false);
        when(userRepository.existsByEmail("rory@gmail.com")).thenReturn(true);

        AlreadyExistsException thrown = assertThrows(AlreadyExistsException.class, () -> {
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

        AlreadyExistsException thrown = assertThrows(AlreadyExistsException.class, () -> {
            userService.updateUser(new UpdateUserDTO(1, "existingUsername", "rory@gmail.com", ""));
        });

        assertEquals("Username already taken: existingusername", thrown.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void updateUserEmailAlreadyUsedTest() {
        
        when(userRepository.getById(1)).thenReturn(user);
        
        when(userRepository.existsByEmail("usedemail@mail.com")).thenReturn(true);

        AlreadyExistsException thrown = assertThrows(AlreadyExistsException.class, () -> {
            userService.updateUser(new UpdateUserDTO(1, "rory", "usedEmail@mail.com", ""));
        });

        assertEquals("Username already taken: usedemail@mail.com", thrown.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }
>>>>>>> 8a76097 (UserServiceTest create et update provisoire)
}
