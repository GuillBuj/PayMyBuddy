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
import com.paymybuddy.paymybuddy.exception.UsernameAlreadyTakenException;
import com.paymybuddy.paymybuddy.exception.BuddyAlreadyAddedException;
import com.paymybuddy.paymybuddy.exception.BuddyNotFoundException;
import com.paymybuddy.paymybuddy.exception.EmailAlreadyUsedException;
import com.paymybuddy.paymybuddy.exception.NotFoundException;
import com.paymybuddy.paymybuddy.exception.SelfAddException;
import com.paymybuddy.paymybuddy.model.Transaction;
import com.paymybuddy.paymybuddy.model.User;
import com.paymybuddy.paymybuddy.repository.TransactionRepository;
import com.paymybuddy.paymybuddy.repository.UserRepository;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Service class responsible for managing user-related operations such as user creation, update, 
 * authentication, buddy management, and transaction handling.
 */
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

    /**
     * Checks if a username already exists in the database.
     * 
     * @param username The username to check.
     * @return true if the username exists, false otherwise.
     */
    public boolean existsByUsername(String username){
        return userRepository.existsByUsername(username.toLowerCase());
    }

    /**
     * Checks if an email already exists in the database.
     * 
     * @param email The email to check.
     * @return true if the email exists, false otherwise.
     */
    public boolean existsByEmail(String email){
        return userRepository.existsByEmail(email.trim().toLowerCase());
    }

    /**
     * Creates a new user based on the provided registration data.
     * This method ensures that the username and email are unique before creating the user.
     * 
     * @param userDTO The registration data for the new user.
     * @return The created {@link User} object.
     * @throws UsernameAlreadyTakenException If the username is already taken.
     * @throws EmailAlreadyUsedException If the email is already used.
     */
    @Transactional
    public User createUser(RegisterUserDTO userDTO){
        log.debug("*** Creating user: {}", userDTO);
        
        String lowerCaseUserName = userDTO.getUsername().toLowerCase();
        if(userRepository.existsByUsername(lowerCaseUserName)){
            log.error("*** Username already taken: {}", lowerCaseUserName);
            throw new UsernameAlreadyTakenException("Username already taken: " + lowerCaseUserName);
        }
        
        String normalizedEmail = userDTO.getEmail().trim().toLowerCase();
        if(userRepository.existsByEmail(normalizedEmail)){
            log.error("*** Email already used: {}", normalizedEmail);
            throw new EmailAlreadyUsedException("Email already used: " + normalizedEmail);
        }
        
        User user = new User();
        user.setUsername(lowerCaseUserName);
        user.setEmail(normalizedEmail);
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        user.setBalance(BigDecimal.ZERO);
        user.setBuddies(new HashSet<>());
        user.setDateCreated(LocalDateTime.now());

        User createdUser = userRepository.save(user);
        log.info("*** User created: {}, {}", createdUser.getEmail(), createdUser.getUsername());
        
        return createdUser;
    }

    /**
     * Updates an existing user's information based on the provided update data.
     * This method validates if the new username or email are unique before updating the user.
     * 
     * @param userDTO The update data for the user.
     * @return The updated {@link User} object.
     * @throws UsernameAlreadyTakenException If the new username is already taken.
     * @throws EmailAlreadyUsedException If the new email is already used.
     */
    @Transactional
    public User updateUser(UpdateUserDTO userDTO){
        log.debug("*** Updating user: {}", userDTO);

        User user = userRepository.getById(userDTO.getId());

        if(!userDTO.getUsername().equals(user.getUsername())){
            String lowerCaseUserName = userDTO.getUsername().toLowerCase();
            if(userRepository.existsByUsername(lowerCaseUserName)){
                log.error("*** Username already taken: {}", lowerCaseUserName);
                throw new UsernameAlreadyTakenException("Username already taken: " + lowerCaseUserName);
            } else user.setUsername(lowerCaseUserName);
        }
        
        if(!userDTO.getEmail().equals(user.getEmail())){
            String normalizedEmail = userDTO.getEmail().trim().toLowerCase();
            if(userRepository.existsByEmail(normalizedEmail)){
                log.error("*** Email already used: {}", normalizedEmail);
                throw new EmailAlreadyUsedException("Email already used: " + normalizedEmail);
            } else user.setEmail(normalizedEmail);
        }
        
        if(!userDTO.getPassword().isEmpty()){
            user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        }
        
        User updatedUser = userRepository.save(user);
        log.info("*** User updated: {}, {}", updatedUser.getEmail(), updatedUser.getUsername());
        
        return updatedUser;
    }

    /**
     * Retrieves a user by their ID.
     * 
     * @param id The ID of the user.
     * @return A {@link UserDTO} containing the user's username, email, and buddies.
     * @throws NotFoundException If no user is found with the given ID.
     */
    public UserDTO getUserById(int id){
        log.debug("*** Getting user by id: {}", id);
        User user = userRepository.findById(id).orElse(null);
        if (user == null){
            throw new NotFoundException("User not found with id " + id);
        }
        return new UserDTO(user.getUsername(), user.getEmail(), user.getBuddies());
    }

    /**
     * Adds a new buddy to the user's buddy list.
     * 
     * @param buddyConnection The buddy connection data, containing the user and buddy emails.
     * @throws SelfAddException If the user tries to add themselves as a buddy.
     * @throws NotFoundException If the user or buddy is not found.
     * @throws BuddyAlreadyAddedException If the buddy already exists in the user's buddy list.
     */
    @Transactional
    public void addBuddy(BuddyConnectionDTO buddyConnection){
        log.debug("*** Adding buddy: {}", buddyConnection);

        if(buddyConnection.userEmail().equals(buddyConnection.buddyEmail())){
            throw new SelfAddException(buddyConnection.userEmail() + " tried to add himself in his list");
        };

        User user = userRepository.findByEmail(buddyConnection.userEmail())
                .orElseThrow(()-> new NotFoundException("User not found with email " + buddyConnection.userEmail()));
                
        User buddy = userRepository.findByEmail(buddyConnection.buddyEmail())
                .orElseThrow(()-> new BuddyNotFoundException("Buddy not found with email " + buddyConnection.buddyEmail(), buddyConnection.buddyEmail()));

        if (user.getBuddies().contains(buddy)){
            throw new BuddyAlreadyAddedException("Buddy connection between " + 
                buddyConnection.userEmail() + " and " + buddyConnection.buddyEmail() + "already exists", buddy.getUsername());
        };
        
        //on considère qu'il n'y a pas de réciprocité ni d'acceptation d'ajout
        user.getBuddies().add(buddy);
        userRepository.save(user);

        log.info("*** Buddy {} added to {} 's list", buddyConnection.buddyEmail(), buddyConnection.userEmail());
    }
  
    /**
     * Removes a buddy from the user's buddy list.
     * 
     * @param buddyConnection The buddy connection data, containing the user and buddy emails.
     * @throws NotFoundException If the user or buddy is not found.
     * @throws NotFoundException If the buddy connection does not exist.
     */
    @Transactional
    public void removeBuddy(BuddyConnectionDTO buddyConnection){
        log.debug("*** Removing buddy: {}", buddyConnection);

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

        log.info("*** Buddy {} removed from {} 's list", buddyConnection.buddyEmail(), buddyConnection.userEmail());
    }
    
    /**
     * Processes a deposit operation by updating the user's balance.
     * 
     * @param operation The balance operation containing the user's email and the amount to deposit.
     */
    @Transactional
    public void deposit(BalanceOperationDTO operation){
        log.debug("*** Processing deposit operation: {}", operation);
        operationService.updateBalance(operation, true);
    }

    /**
     * Retrieves a paginated list of transactions for a user.
     * 
     * @param user The user whose transactions are to be retrieved.
     * @param page The page number to retrieve.
     * @param size The number of transactions per page.
     * @return A paginated {@link Page} of {@link TransactionInListDTO} objects.
     */
    public Page<TransactionInListDTO> getTransactionsPaginated(User user, int page, int size){
        return getTransactionsPaginated(user.getId(), page, size);
    }
    
    /**
     * Retrieves a paginated list of transactions for a user by their user ID.
     * 
     * @param userId The user ID whose transactions are to be retrieved.
     * @param page The page number to retrieve.
     * @param size The number of transactions per page.
     * @return A paginated {@link Page} of {@link TransactionInListDTO} objects.
     */
    public Page<TransactionInListDTO> getTransactionsPaginated(int userId, int page, int size){
        log.debug("*** Getting transactions for userId: {}", userId);

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

    /**
     * Retrieves a user by their email.
     * 
     * @param email The email of the user to retrieve.
     * @return The {@link User} object associated with the given email.
     * @throws UsernameNotFoundException If no user is found with the provided email.
     */
    public User getUserByEmail(String email) {
        log.debug("*** Getting user by email: {}", email);

        return userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("Utilisateur non trouvé: " + email));
    }

    /**
     * Authenticates a user with the provided email and password.
     * 
     * @param request The HTTP request, used for context.
     * @param email The email of the user to authenticate.
     * @param password The password of the user to authenticate.
     * @throws AuthenticationException If the authentication fails.
     */
    public void authenticateUser(HttpServletRequest request, String email, String password){

        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(email, password);
        Authentication authentication = authenticationManager.authenticate(token);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        log.info("* User connected: {}", email);
    }

    /**
     * Retrieves the list of buddies for a user, based on the user's ID.
     * 
     * @param userId The ID of the user whose buddies are to be retrieved.
     * @return A {@link BuddiesDTO} object containing the list of buddies for the user.
     * @throws EntityNotFoundException If no user is found with the given ID.
     */
    public BuddiesDTO getBuddies(int userId) {
        log.debug("*** Getting buddies for userId: {}", userId);

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