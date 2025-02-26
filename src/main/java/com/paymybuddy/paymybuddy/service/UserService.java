package com.paymybuddy.paymybuddy.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.paymybuddy.paymybuddy.dto.BalanceOperationDTO;
import com.paymybuddy.paymybuddy.dto.BuddiesDTO;
import com.paymybuddy.paymybuddy.dto.BuddyConnectionDTO;
import com.paymybuddy.paymybuddy.dto.BuddyForTransferDTO;
import com.paymybuddy.paymybuddy.dto.RegisterUserDTO;
import com.paymybuddy.paymybuddy.dto.TransactionInListDTO;
import com.paymybuddy.paymybuddy.dto.UpdateUserDTO;
import com.paymybuddy.paymybuddy.dto.UserDTO;
import com.paymybuddy.paymybuddy.exception.AlreadyExistsException;
import com.paymybuddy.paymybuddy.exception.NotFoundException;
import com.paymybuddy.paymybuddy.model.Transaction;
import com.paymybuddy.paymybuddy.model.User;
import com.paymybuddy.paymybuddy.repository.TransactionRepository;
import com.paymybuddy.paymybuddy.repository.UserRepository;

import jakarta.persistence.EntityNotFoundException;
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

    @Transactional
    public User updateUser(UpdateUserDTO userDTO){
        
        User user = userRepository.getById(userDTO.getId());

        if(!userDTO.getUsername().equals(user.getUsername())){
            String lowerCaseUserName = userDTO.getUsername().toLowerCase();
            if(userRepository.existsByUsername(lowerCaseUserName)){
                log.error("Username already taken: {}", lowerCaseUserName);
                throw new AlreadyExistsException("Username already taken: " + lowerCaseUserName);
            } else user.setUsername(lowerCaseUserName);
        }
        
        if(!userDTO.getEmail().equals(user.getEmail())){
            String normalizedEmail = userDTO.getEmail().trim().toLowerCase();
            if(userRepository.existsByEmail(normalizedEmail)){
                log.error("Email already used: {}", normalizedEmail);
                throw new AlreadyExistsException("Email already used:" + normalizedEmail);
            } else user.setEmail(normalizedEmail);
        }
        
        if(userDTO.getPassword()!=""){
            user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        }
        
        log.info("User updated: {}", user.getEmail());
        
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

    // public TransactionListDTO getTransactions(User user){
    //     return getTransactions(user.getId());
    // }
    
    // public TransactionListDTO getTransactions(int userId){
    //     List<TransactionInListDTO> transactions = transactionRepository
    //         .findBySenderIdOrReceiverIdOrderByDateCreatedDesc(userId, userId)
    //         .stream()
    //         .map(transaction -> new TransactionInListDTO(
    //             transaction.getDateCreated(),
    //             transaction.getSender().getUsername(),
    //             transaction.getReceiver().getUsername(),
    //             transaction.getAmount(),
    //             transaction.getDescription()
    //         ))
    //         .collect(Collectors.toList());

    //     return new TransactionListDTO(transactions);
    // }

    public Page<TransactionInListDTO> getTransactionsPaginated(User user, int page, int size){
        return getTransactionsPaginated(user.getId(), page, size);
    }
    
    public Page<TransactionInListDTO> getTransactionsPaginated(int userId, int page, int size){
        Pageable pageable = PageRequest.of(page, size, Sort.by("dateCreated").descending());

        Page<Transaction> transactionsPage =
            transactionRepository.findBySenderIdOrReceiverIdOrderByDateCreatedDesc(userId, userId, pageable);

        return transactionsPage.map(transaction -> new TransactionInListDTO(
            transaction.getDateCreated(),
            transaction.getSender().getUsername(),
            transaction.getReceiver().getUsername(),
            transaction.getAmount(),
            transaction.getDescription()
        ));
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("Utilisateur non trouvé: " + email));
    }

    public void authenticateUser(HttpServletRequest request, String email, String password){

        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(email, password);

        Authentication authentication = authenticationManager.authenticate(token);

        SecurityContextHolder.getContext().setAuthentication(authentication);

    }

    public BuddiesDTO getBuddies(int userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new EntityNotFoundException("Utilisateur non trouvé"));

        return new BuddiesDTO(user.getBuddies()
                            .stream()
                            .map(buddy -> new BuddyForTransferDTO(
                                buddy.getId(),
                                buddy.getUsername(),
                                buddy.getEmail()
                            ))
                            .collect(Collectors.toSet()));
    }
}
