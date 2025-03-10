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

import static org.hamcrest.Matchers.is;
import static org.hamcrest.beans.HasPropertyWithValue.hasProperty;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.paymybuddy.paymybuddy.dto.BuddiesDTO;
import com.paymybuddy.paymybuddy.dto.BuddyForTransferDTO;
import com.paymybuddy.paymybuddy.dto.UpdateUserDTO;
import com.paymybuddy.paymybuddy.model.User;
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
        String oldEncodedPassword = "$2a$10$Wj8wft8eK9F3CZyZ.xYZwTw2LGlJKl5Ejq5fnjFupm17Xx5PAkjr6";

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
            .andExpect(flash().attribute("errorMessage", Matchers.containsString("mot de passe")))
            ;
        
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
}
