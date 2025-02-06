package com.paymybuddy.paymybuddy.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "account_id")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToOne
    @JoinColumn(name = "user_account_id", nullable = true)
    private UserAccount userAccount;

    @OneToOne
    @JoinColumn(name = "bank_account_id", nullable = true)
    private BankAccount bankAccount;

    @PrePersist
    @PreUpdate
    private void validateAccount(){
        if((userAccount != null && bankAccount != null) || (userAccount == null && bankAccount == null)){
            throw new IllegalStateException("An account must be linked to either a user account or a bank account, but not both");
        }
    }
}
