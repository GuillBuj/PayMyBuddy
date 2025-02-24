package com.paymybuddy.paymybuddy.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.paymybuddy.paymybuddy.dto.BalanceOperationDTO;
import com.paymybuddy.paymybuddy.dto.BuddyConnectionDTO;
import com.paymybuddy.paymybuddy.dto.RegisterUserDTO;
import com.paymybuddy.paymybuddy.dto.TransactionInList;
import com.paymybuddy.paymybuddy.dto.TransactionListDTO;
import com.paymybuddy.paymybuddy.dto.UserDTO;
import com.paymybuddy.paymybuddy.exception.AlreadyExistsException;
import com.paymybuddy.paymybuddy.exception.NotFoundException;
import com.paymybuddy.paymybuddy.model.User;
import com.paymybuddy.paymybuddy.repository.TransactionRepository;
import com.paymybuddy.paymybuddy.repository.UserRepository;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@AllArgsConstructor
@Slf4j
public class UserService {
    
    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;

    private final PasswordEncoder passwordEncoder;

    private final BalanceService operationService;

    @Autowired
    private AuthenticationManager authenticationManager;

    public boolean existsByUsername(String username){
        return userRepository.existsByUsername(username.toLowerCase());
    }

    public boolean existsByEmail(String email){
        return userRepository.existsByEmail(email.trim().toLowerCase());
    }

    @Transactional
    public User createUser(RegisterUserDTO userDTO){
        
        String lowerCaseUserName = userDTO.getUsername().toLowerCase();
        if(userRepository.existsByUsername(lowerCaseUserName)){
            log.error("Username already taken: {}", lowerCaseUserName);
            throw new AlreadyExistsException("Username already taken: " + lowerCaseUserName);
        }
        
        String normalizedEmail = userDTO.getEmail().trim().toLowerCase();
        if(userRepository.existsByEmail(normalizedEmail)){
            log.error("Email already used: {}", normalizedEmail);
            throw new AlreadyExistsException("Email already used:" + normalizedEmail);
        }
        
        User user = new User();
        user.setUsername(lowerCaseUserName);
        user.setEmail(normalizedEmail);
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        user.setBalance(BigDecimal.ZERO);
        user.setBuddies(new HashSet<>());
        user.setDateCreated(LocalDateTime.now());

        log.info("User created: {}", user.getEmail());
        
        return userRepository.save(user);
    }

    public UserDTO getUserById(int id){
        User user = userRepository.findById(id).orElse(null);
        if (user == null){
            throw new NotFoundException("User not found with id " + id);
        }
        return new UserDTO(user.getUsername(), user.getEmail(), user.getBuddies());
    }

    //on considère qu'il n'y a pas de réciprocité ni d'acceptation d'ajout
    @Transactional
    public void addBuddy(BuddyConnectionDTO buddyConnection){
        User user = userRepository.findByEmail(buddyConnection.userEmail())
                .orElseThrow(()-> new NotFoundException("User not found with email " + buddyConnection.userEmail()));
                
        User buddy = userRepository.findByEmail(buddyConnection.buddyEmail())
                .orElseThrow(()-> new NotFoundException("Buddy not found with email " + buddyConnection.buddyEmail()));

        if (user.getBuddies().contains(buddy)){
            throw new AlreadyExistsException("Buddy connection between " + 
                buddyConnection.userEmail() + " and " + buddyConnection.buddyEmail() + "already exists");
        };
        
        user.getBuddies().add(buddy);

        userRepository.save(user);
    }
  

    @Transactional
    public void removeBuddy(BuddyConnectionDTO buddyConnection){
        User user = userRepository.findByEmail(buddyConnection.userEmail())
                .orElseThrow(()-> new NotFoundException("User not found with email " + buddyConnection.userEmail()));
                
        User buddy = userRepository.findByEmail(buddyConnection.buddyEmail())
                .orElseThrow(()-> new NotFoundException("Buddy not found with email " + buddyConnection.buddyEmail()));
        
        if (!user.getBuddies().contains(buddy)){
            throw new NotFoundException("Buddy connection between " + 
                buddyConnection.userEmail() + " and " + buddyConnection.buddyEmail() + " does not exist");
        }
        
        user.getBuddies().remove(buddy);

        userRepository.save(user);
    }
    
    @Transactional
    public void deposit(BalanceOperationDTO operation){
        operationService.updateBalance(operation, true);
    }

    @Transactional
    public void withdraw(BalanceOperationDTO operation){
        operationService.updateBalance(operation, false);
    }

    public TransactionListDTO getTransactions(User user){
        return getTransactions(user.getId());
    }
    
    public TransactionListDTO getTransactions(int id){
        List<TransactionInList> transactions = transactionRepository
            .findBySenderIdOrReceiverIdOrderByDateCreatedDesc(id, id)
            .stream()
            .map(transaction -> new TransactionInList(
                transaction.getDateCreated(),
                transaction.getSender().getUsername(),
                transaction.getReceiver().getUsername(),
                transaction.getAmount(),
                transaction.getDescription()
            ))
            .collect(Collectors.toList());

        return new TransactionListDTO(transactions);
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
    }

    public void authenticateUser(HttpServletRequest request, String email, String password){

        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(email, password);

        Authentication authentication = authenticationManager.authenticate(token);

        SecurityContextHolder.getContext().setAuthentication(authentication);

    }
}
