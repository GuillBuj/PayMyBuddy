package com.paymybuddy.paymybuddy.model;

import java.math.BigDecimal;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "user_account")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserAccount {
    @Id
    private Integer userId;

    @OneToOne
    @MapsId //pour que l'ID soit le même que celui de User
    @JoinColumn(name = "USER_id")
    private User user;

    private BigDecimal balance;

    @OneToOne(mappedBy = "userAccount")
    private Account account;
}
