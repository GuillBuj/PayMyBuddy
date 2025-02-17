package com.paymybuddy.paymybuddy.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record TransactionRequestDTO(
    @NotNull @Email String senderEmail,
    @NotNull @Email String receiverEmail,
    @NotNull @Positive BigDecimal amount,
    String description
) {

}
