package com.paymybuddy.paymybuddy.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

public record TransactionInList(
    LocalDateTime dateCreated,
    String senderName,
    String receiverName,
    BigDecimal amount,
    String description
) {

}
