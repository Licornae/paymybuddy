package com.openclassrooms.paymybuddy.dto;


/**
 * Data Transfer Object used to display transaction information.
 * This DTO represents a transaction visible to the user,
 * typically in the transfer history view.
 *
 * @param receiver    the username of the transaction recipient
 * @param description the transaction description
 * @param amount      the transferred amount
 */
public record TransactionViewDTO(
        String receiver,
        String description,
        Double amount
) {}