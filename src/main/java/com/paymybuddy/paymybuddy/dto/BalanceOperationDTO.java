package com.paymybuddy.paymybuddy.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record BalanceOperationDTO(
    @NotNull @Email String userEmail,
    @NotNull @Positive BigDecimal amount
) {

}
