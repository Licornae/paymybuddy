package com.openclassrooms.paymybuddy.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object used to capture transfer form input.
 * This DTO validates user input when initiating a money transfer.
 * It ensures that:
 * - A receiver is selected
 * - The amount is positive and properly formatted
 * - A description is provided
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransferFormDTO {

    @NotNull
    private Integer receiverId;

    @Positive
    @DecimalMin(value = "0.01", message = "Le montant doit être supérieur à 0")
    @Digits(integer = 10, fraction = 2,
            message = "Le montant ne peut contenir que 2 décimales maximum")
    private Double amount;

    @NotBlank
    private String description;
}
