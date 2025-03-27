package com.paymybuddy.paymybuddy.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a user in the system.
 * Contains the user's personal details, balance, and buddy relationships with other users.
 */
@Entity
@Table(name = "user")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "user_name", nullable = false, unique = true)
    private String username;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    @JsonIgnore
    private String password;

    @Column(nullable = false, precision = 9, scale = 2)
    private BigDecimal balance;

    @ManyToMany
    @JoinTable(
        name = "buddies",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "buddy_id"))
    @JsonIgnore
    private Set<User> buddies;

    @Column(name = "date_created", nullable = false, updatable = false)
    private LocalDateTime dateCreated;

    // évite erreur boucle lors d'un ajout de relation déjà existante dans l'autre sens
    @Override
    public int hashCode(){
        return Objects.hash(id);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return id != null && id.equals(user.id);
    }
}
