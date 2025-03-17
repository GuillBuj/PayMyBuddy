package com.paymybuddy.paymybuddy.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "transaction")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private BigDecimal amount;

    @ManyToOne
    @JoinColumn(name = "sender_id", nullable = false)
    private User sender;

    @ManyToOne
    @JoinColumn(name = "receiver_id", nullable = false)
    private User receiver;

    private String description;

    private BigDecimal fee;

    @Column(name = "date_created")
    private LocalDateTime dateCreated;

    @Override
    public String toString() {
        return String.format(
            "Transaction{id=%d, sender=%s, receiver=%s, amount=%s, fee=%s, description='%s', dateCreated=%s}",
            id, sender.getUsername(), receiver.getUsername(), amount, fee, description, dateCreated
        );
    }
}
