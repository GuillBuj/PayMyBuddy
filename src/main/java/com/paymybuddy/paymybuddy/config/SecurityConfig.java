package com.paymybuddy.paymybuddy.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig{

    // @Bean
    // public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
    //     http.authorizeHttpRequests()
    //             .requestMatchers("/login", "/register").permitAll()
    //             .anyRequest().authenticated()
    //         .and()
    //         .formLogin().permitAll()
    //         .and()
    //         .logout().permitAll();

    //     return http.build();
    // }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf().disable() // Désactive CSRF (à activer plus tard avec token CSRF)
            .authorizeHttpRequests()
                .requestMatchers("/register","/user/**").permitAll() // Autorise l'inscription sans authentification
                .anyRequest().authenticated() // Protège toutes les autres routes
            .and()
            .formLogin()
            .and()
            .logout();

        return http.build();
    }
}
