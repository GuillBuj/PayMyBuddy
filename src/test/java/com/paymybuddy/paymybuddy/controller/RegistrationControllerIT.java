package com.paymybuddy.paymybuddy.controller;

import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.containsString;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import com.paymybuddy.paymybuddy.model.User;
import com.paymybuddy.paymybuddy.repository.UserRepository;

import jakarta.transaction.Transactional;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Sql(scripts = "/paymybuddy_test.session.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Transactional
public class RegistrationControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    public void showRegistrationFormGetIT() throws Exception {
        
        mockMvc.perform(get("/register"))
                .andExpect(status().isOk())
                .andExpect(view().name("register"))
                .andExpect(model().attributeExists("userDTO"));
    }

    @Test
    public void registerOkIT() throws Exception {

        String email = "newuser@example.com";
        String username = "newuser";
        String password = "Password123";

        mockMvc.perform(post("/register")
                        .with(csrf())
                        .param("email", email)
                        .param("username", username)
                        .param("password", password))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"))
                .andExpect(flash().attributeExists("successMessage"));

        User user = userRepository.findByEmail(email).orElse(null);
        assertNotNull(user);
        assertEquals(username, user.getUsername());
        assertTrue(passwordEncoder.matches(password, user.getPassword()));
    }

    @Test
    public void registerValidationErrorsIT() throws Exception {

        String invalidEmail = "newuser-example.com";
        String invalidUsername = "x";
        String invalidPassword = "xx";

        mockMvc.perform(post("/register")
                        .with(csrf())
                        .param("email", invalidEmail)
                        .param("username", invalidUsername)
                        .param("password", invalidPassword))
                .andExpect(status().isOk())
                .andExpect(view().name("register"))
                .andExpect(model().attributeHasFieldErrors("userDTO", "email", "username", "password"));

        User user = userRepository.findByEmail(invalidEmail).orElse(null);
        assertNull(user);
    }

    @Test
    public void registerEmailValidationErrorIT() throws Exception {

        String invalidEmail = "newuser-example.com";
        String username = "newuser";
        String password = "Password123";

        mockMvc.perform(post("/register")
                        .with(csrf())
                        .param("email", invalidEmail)
                        .param("username", username)
                        .param("password", password))
                .andExpect(status().isOk())
                .andExpect(view().name("register"))
                .andExpect(model().attributeHasFieldErrors("userDTO", "email"));

        User user = userRepository.findByEmail(invalidEmail).orElse(null);
        assertNull(user);
    }

    @Test
    public void registerUsernameValidationErrorIT() throws Exception {

        String email = "newuser@example.com";
        String invalidUsername = "";
        String password = "Password123";

        mockMvc.perform(post("/register")
                        .with(csrf())
                        .param("email", email)
                        .param("username", invalidUsername)
                        .param("password", password))
                .andExpect(status().isOk())
                .andExpect(view().name("register"))
                .andExpect(model().attributeHasFieldErrors("userDTO", "username"));

        User user = userRepository.findByEmail(email).orElse(null);
        assertNull(user);
    }

    @Test
    public void registerPasswordValidationErrorIT() throws Exception {

        String email = "newuser@example.com";
        String username = "newuser";
        String invalidPassword = "xx";

        mockMvc.perform(post("/register")
                        .with(csrf())
                        .param("email", email)
                        .param("username", username)
                        .param("password", invalidPassword))
                .andExpect(status().isOk())
                .andExpect(view().name("register"))
                .andExpect(model().attributeHasFieldErrors("userDTO", "password"));

        User user = userRepository.findByEmail(email).orElse(null);
        assertNull(user);
    }

    @Test
    public void registerEmailAlreadyUsedErrorIT() throws Exception {

        String alreadyUsedEmail = "rory@gmail.com";
        String username = "newuser";
        String password = "Password123";

        mockMvc.perform(post("/register")
                        .with(csrf())
                        .param("email", alreadyUsedEmail)
                        .param("username", username)
                        .param("password", password))
                        .andExpect(status().is3xxRedirection())
                        .andExpect(flash().attributeExists("errorMessage"))
                        .andExpect(flash().attribute("errorMessage", containsString("mail")));

        User user = userRepository.findByEmail(alreadyUsedEmail).orElse(null);
        assertNotNull(user);
        assertNotEquals(username, user.getUsername());
    }

    @Test
    public void registerUsernameAlreadyTakenErrorIT() throws Exception {

        String email = "newemail@gmail.com";
        String alreadyTakenUsername = "rory";
        String password = "Password123";

        mockMvc.perform(post("/register")
                        .with(csrf())
                        .param("email", email)
                        .param("username", alreadyTakenUsername)
                        .param("password", password))
                        .andExpect(status().is3xxRedirection())
                        .andExpect(flash().attributeExists("errorMessage"));

        User user = userRepository.findByEmail(email).orElse(null);
        assertNull(user);
    }
}
