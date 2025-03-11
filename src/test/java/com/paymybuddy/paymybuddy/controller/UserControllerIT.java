package com.paymybuddy.paymybuddy.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.Set;

import org.hamcrest.Matchers;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;


import static org.hamcrest.Matchers.*;
import static org.hamcrest.beans.HasPropertyWithValue.hasProperty;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.paymybuddy.paymybuddy.dto.BuddiesDTO;
import com.paymybuddy.paymybuddy.dto.BuddyForTransferDTO;
import com.paymybuddy.paymybuddy.dto.UpdateUserDTO;
import com.paymybuddy.paymybuddy.model.Transaction;
import com.paymybuddy.paymybuddy.model.User;
import com.paymybuddy.paymybuddy.repository.TransactionRepository;
import com.paymybuddy.paymybuddy.repository.UserRepository;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;


@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Sql(scripts = "/paymybuddy_test.session.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Transactional
@Slf4j
public class UserControllerIT {
    
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;


    @Test
    @WithMockUser(username = "rory@gmail.com")
    void getProfileGetIT() throws Exception {
        
        mockMvc.perform(get("/user/profile"))
            .andExpect(status().isOk())
            .andExpect(view().name("profile"))
            .andExpect(model().attribute("username", "rory"))
            .andExpect(model().attribute("email", "rory@gmail.com"))
            .andExpect(model().attribute("editMode", false));
    }

    @Test
    @WithMockUser(username = "rory@gmail.com")
    void editProfileGetIT() throws Exception {
        
        mockMvc.perform(get("/user/profile/edit"))
                .andExpect(status().isOk())
                .andExpect(view().name("profile"))
                .andExpect(model().attributeExists("updateUserDTO"))
                .andExpect(model().attribute("updateUserDTO", hasProperty("username", is("rory"))))
                .andExpect(model().attribute("updateUserDTO", hasProperty("email", is("rory@gmail.com"))))
                .andExpect(model().attribute("editMode", true));
    }

    @Test
    @WithMockUser(username = "rory@gmail.com")
    void updateProfileIT() throws Exception {
        
        String newUsername = "newusername";
        String newEmail = "newemail@gmail.com";
        String newPassword = "newPassword123";

        mockMvc.perform(patch("/user/profile")
                        .with(csrf()) 
                        .flashAttr("updateUserDTO", new UpdateUserDTO(1, newUsername, newEmail, newPassword)))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/user/profile"));
        
        User updatedUser = userRepository.findById(1).orElseThrow();

        assertEquals(newUsername, updatedUser.getUsername());
        assertEquals(newEmail, updatedUser.getEmail());
        assertTrue(passwordEncoder.matches(newPassword, updatedUser.getPassword()));
    }

    @Test
    @WithMockUser(username = "rory@gmail.com")
    void updateProfileWithoutPasswordIT() throws Exception {
        
        String newUsername = "newusername";
        String newEmail = "newemail@gmail.com";
        String oldEncodedPassword = "$2b$12$NPO6GqMCfpmqlIQZCA7K4.QfY0G1uLbtvvHpjwz8NqmtOm1W3a8ke";

        mockMvc.perform(patch("/user/profile")
                        .with(csrf()) 
                        .flashAttr("updateUserDTO", new UpdateUserDTO(1, newUsername, newEmail, "")))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/user/profile"));
        
        User updatedUser = userRepository.findById(1).orElseThrow();

        assertEquals(newUsername, updatedUser.getUsername());
        assertEquals(newEmail, updatedUser.getEmail());
        assertEquals(oldEncodedPassword, updatedUser.getPassword());
    }

    @Test
    @WithMockUser(username = "rory@gmail.com")
    void updateProfilePasswordTooShortIT() throws Exception {
        
        String newUsername = "newusername";
        String newEmail = "newemail@gmail.com";
        String newPasswordTooShort = "12";

        mockMvc.perform(patch("/user/profile")
                        .with(csrf()) 
                        .flashAttr("updateUserDTO", new UpdateUserDTO(1, newUsername, newEmail, newPasswordTooShort)))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/user/profile/edit"))
                .andExpect(flash().attributeExists("errorMessage"))
                .andExpect(flash().attribute("errorMessage", Matchers.containsString("mot de passe")));
        
        User updatedUser = userRepository.findById(1).orElseThrow();

        assertNotEquals(newUsername, updatedUser.getUsername());
        assertNotEquals(newEmail, updatedUser.getEmail());
        assertNotEquals(newPasswordTooShort, updatedUser.getPassword());
    }

    @Test
    @WithMockUser(username = "rory@gmail.com")
    void updateProfileEmailValidationErrorIT() throws Exception {
        
        String newUsername = "newusername";
        String newEmailValidationError = "newemailvalidationerror";
        String newPassword = "newPassword123";

        mockMvc.perform(patch("/user/profile")
                        .with(csrf()) 
                        .flashAttr("updateUserDTO", new UpdateUserDTO(1, newUsername, newEmailValidationError, newPassword)))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/user/profile/edit"))
                .andExpect(flash().attributeExists("emailError"));
        
        User updatedUser = userRepository.findById(1).orElseThrow();

        assertNotEquals(newUsername, updatedUser.getUsername());
        assertNotEquals(newEmailValidationError, updatedUser.getEmail());
        assertFalse(passwordEncoder.matches(newPassword, updatedUser.getPassword()));
    }

    @Test
    @WithMockUser(username = "rory@gmail.com")
    void showRelationFormGetIT() throws Exception {

        BuddiesDTO expectedBuddies = new BuddiesDTO(Set.of(
                new BuddyForTransferDTO(2, "jimi", "jimi@gmail.com"),
                new BuddyForTransferDTO(3, "stevie", "stevie@gmail.com")));

        mockMvc.perform(get("/user/relation"))
                .andExpect(status().isOk())
                .andExpect(view().name("relation"))
                .andExpect(model().attribute("buddies", expectedBuddies));
    }

    @Test
    @WithMockUser(username = "stevie@gmail.com")
    void addBuddyOkIT() throws Exception {

        String buddyEmail = "jimi@gmail.com";

        mockMvc.perform(post("/user/relation")
                        .with(csrf()) 
                        .param("buddyEmail", buddyEmail))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/user/relation"))
                .andExpect(flash().attributeExists("successMessage"));

        User user = userRepository.getById(3);
        User buddy = userRepository.getById(2);

        assertTrue(user.getBuddies().contains(buddy));
    }

    @Test
    @WithMockUser(username = "rory@gmail.com")
    void addBuddyAlreadyAddedIT() throws Exception {

        String buddyEmail = "jimi@gmail.com";

        User user = userRepository.getById(1);
        int initialBuddiesNumber = user.getBuddies().size();
        
        mockMvc.perform(post("/user/relation")
                        .with(csrf()) 
                        .param("buddyEmail", buddyEmail))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/user/relation"))
                .andExpect(flash().attributeExists("errorMessage"));

        User updatedUser = userRepository.getById(1);
        int updatedBuddiesNumber = updatedUser.getBuddies().size();

        assertEquals(initialBuddiesNumber, updatedBuddiesNumber);    
    }

    @Test
    @WithMockUser(username = "rory@gmail.com")
    void addBuddyNotExistingIT() throws Exception {

        String notExistingbuddyEmail = "notexisting@gmail.com";

        User user = userRepository.getById(1);
        int initialBuddiesNumber = user.getBuddies().size();
        
        mockMvc.perform(post("/user/relation")
                        .with(csrf()) 
                        .param("buddyEmail", notExistingbuddyEmail))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/user/relation"))
                .andExpect(flash().attributeExists("errorMessage"));

        User updatedUser = userRepository.getById(1);
        int updatedBuddiesNumber = updatedUser.getBuddies().size();

        assertEquals(initialBuddiesNumber, updatedBuddiesNumber);   
    }

    @Test
    @WithMockUser(username = "rory@gmail.com")
    void removeBuddyOkIT() throws Exception {

        int buddyId = 2;

        User user = userRepository.getById(1);
        int initialBuddiesNumber = user.getBuddies().size();
        
        mockMvc.perform(delete("/user/relation/{id}", buddyId)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/user/relation"));

        User updatedUser = userRepository.getById(1);
        int updatedBuddiesNumber = updatedUser.getBuddies().size();
        User buddy = userRepository.getById(2);

        assertFalse(updatedUser.getBuddies().contains(buddy));
        assertEquals(initialBuddiesNumber - 1, updatedBuddiesNumber);    
    }

    @Test
    @WithMockUser(username = "rory@gmail.com")
    void showTransferFormGetIT() throws Exception {
        
        BuddiesDTO buddies = new BuddiesDTO(Set.of(
                new BuddyForTransferDTO(2, "jimi", "jimi@gmail.com"),
                new BuddyForTransferDTO(3, "stevie", "stevie@gmail.com")));

        mockMvc.perform(get("/user/transfer")
                        .param("page", "0")
                        .param("size", "8"))
                .andExpect(status().isOk())
                .andExpect(view().name("transfer"))
                .andExpect(model().attribute("balance", comparesEqualTo(BigDecimal.valueOf(1000.00))))
                .andExpect(model().attribute("transactions", hasProperty("content",hasSize(3))))
                .andExpect(model().attribute("transactions", hasProperty("number", is(0))))
                .andExpect(model().attribute("transactions", hasProperty("totalPages", is(1))))
                .andExpect(model().attribute("transactions", hasProperty("content", not(empty()))))
                .andExpect(model().attribute("buddies", buddies))
                .andExpect(model().attribute("currentPage", 0))
                .andExpect(model().attribute("totalPages", 1));
    }

    @Test
    @WithMockUser(username = "rory@gmail.com")
    void handleTransferOkIT() throws Exception {
        
        String userEmail = "rory@gmail.com";
        String buddyEmail = "jimi@gmail.com";
        BigDecimal amount = new BigDecimal("500.00");
        String description = "Test";

        mockMvc.perform(post("/user/transfer")
                        .param("buddy", buddyEmail)
                        .param("amount", amount.toString())
                        .param("description", description)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/user/transfer"))
                .andExpect(flash().attributeExists("successMessage"));

        User updatedUser = userRepository.findByEmail(userEmail).orElseThrow();
        User updatedBuddy = userRepository.findByEmail(buddyEmail).orElseThrow();

        assertEquals(BigDecimal.valueOf(500.00).setScale(2), updatedUser.getBalance().setScale(2));
        assertEquals(BigDecimal.valueOf(1500.00).setScale(2), updatedBuddy.getBalance().setScale(2));


        Optional<Transaction> lastTransaction = transactionRepository.findTopByOrderByDateCreatedDesc();
        assertTrue(lastTransaction.isPresent());
        Transaction transaction = lastTransaction.get();
        assertEquals(userEmail, transaction.getSender().getEmail());
        assertEquals(buddyEmail, transaction.getReceiver().getEmail());
        assertEquals(amount, transaction.getAmount());
        assertEquals(description, transaction.getDescription());
    }

    @Test
    @WithMockUser(username = "rory@gmail.com")
    void handleTransferNegativeTransactionIT() throws Exception {
        
        String userEmail = "rory@gmail.com";
        String buddyEmail = "jimi@gmail.com";
        BigDecimal negativeAmount = new BigDecimal("-100.00");
        String description = "Negative test";

        mockMvc.perform(post("/user/transfer")
                        .param("buddy", buddyEmail)
                        .param("amount", negativeAmount.toString())
                        .param("description", description)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/user/transfer"))
                .andExpect(flash().attributeExists("errorMessage"));

        User updatedUser = userRepository.findByEmail(userEmail).orElseThrow();
        User updatedBuddy = userRepository.findByEmail(buddyEmail).orElseThrow();

        assertEquals(BigDecimal.valueOf(1000.00).setScale(2), updatedUser.getBalance().setScale(2));
        assertEquals(BigDecimal.valueOf(1000.00).setScale(2), updatedBuddy.getBalance().setScale(2));

        Optional<Transaction> lastTransaction = transactionRepository.findTopByOrderByDateCreatedDesc();
        assertTrue(lastTransaction.isPresent());
        Transaction transaction = lastTransaction.get();
        assertNotEquals(negativeAmount, transaction.getAmount());
        assertNotEquals(description, transaction.getDescription());
    }

    @Test
    @WithMockUser(username = "rory@gmail.com")
    void handleTransferNotEnoughMoneyIT() throws Exception {
        
        String userEmail = "rory@gmail.com";
        String buddyEmail = "jimi@gmail.com";
        BigDecimal tooMuchAmount = new BigDecimal("10000.00");
        String description = "Not enough money test";

        mockMvc.perform(post("/user/transfer")
                        .param("buddy", buddyEmail)
                        .param("amount", tooMuchAmount.toString())
                        .param("description", description)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/user/transfer"))
                .andExpect(flash().attributeExists("errorMessage"));

        User updatedUser = userRepository.findByEmail(userEmail).orElseThrow();
        User updatedBuddy = userRepository.findByEmail(buddyEmail).orElseThrow();

        assertEquals(BigDecimal.valueOf(1000.00).setScale(2), updatedUser.getBalance().setScale(2));
        assertEquals(BigDecimal.valueOf(1000.00).setScale(2), updatedBuddy.getBalance().setScale(2));

        Optional<Transaction> lastTransaction = transactionRepository.findTopByOrderByDateCreatedDesc();
        assertTrue(lastTransaction.isPresent());
        Transaction transaction = lastTransaction.get();
        assertNotEquals(tooMuchAmount, transaction.getAmount());
        assertNotEquals(description, transaction.getDescription());
    }

    @Test
    @WithMockUser(username = "rory@gmail.com")
    public void depositGetIT() throws Exception {
        
        mockMvc.perform(get("/user/deposit"))
               .andExpect(status().isOk())
               .andExpect(view().name("deposit"))
               .andExpect(model().attributeExists("balanceOperationDTO"));
    }

    @Test
    @WithMockUser(username = "rory@gmail.com")
    public void depositPostOkIT() throws Exception {
        
        mockMvc.perform(post("/user/deposit")
                        .with(csrf())
                .param("userEmail", "rory@gmail.com")
                .param("amount", "100.00"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/user/transfer"))
                .andExpect(flash().attributeExists("successMessage"));
    }
}
