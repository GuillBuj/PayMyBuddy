package com.paymybuddy.paymybuddy.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;


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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.paymybuddy.paymybuddy.PaymybuddyApplication;
import com.paymybuddy.paymybuddy.dto.UpdateUserDTO;
import com.paymybuddy.paymybuddy.model.User;
import com.paymybuddy.paymybuddy.repository.UserRepository;

import jakarta.transaction.Transactional;


@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Sql(scripts = "/paymybuddy_test.session.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Transactional
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
}
