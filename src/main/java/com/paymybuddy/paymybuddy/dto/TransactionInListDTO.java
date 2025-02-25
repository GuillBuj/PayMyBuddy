package com.paymybuddy.paymybuddy.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;


public record TransactionInListDTO(
    LocalDateTime dateCreated,
    String senderName,
    String receiverName,
    BigDecimal amount,
    String description
) {

}
