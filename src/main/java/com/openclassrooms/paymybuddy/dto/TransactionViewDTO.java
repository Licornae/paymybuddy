package com.openclassrooms.paymybuddy.dto;

public record TransactionViewDTO(
        String receiver,
        String description,
        Double amount
) {}