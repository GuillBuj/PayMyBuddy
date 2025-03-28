package com.paymybuddy.paymybuddy.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

public record BuddyConnectionDTO(
    @NotNull @Email String userEmail,
    @NotNull @Email String buddyEmail
) {
}
