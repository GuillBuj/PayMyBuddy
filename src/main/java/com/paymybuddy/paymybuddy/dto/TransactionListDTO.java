package com.paymybuddy.paymybuddy.dto;

import java.util.List;

import com.paymybuddy.paymybuddy.model.Transaction;

import lombok.AllArgsConstructor;

public record TransactionListDTO(List<TransactionInList> transactions) {


}
