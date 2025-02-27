package com.paymybuddy.paymybuddy.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.paymybuddy.paymybuddy.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Integer>{
    
    User getById(int id);

    Optional<User> findByEmail(String email);
    Optional<User> findByUsername(String username);

    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}
