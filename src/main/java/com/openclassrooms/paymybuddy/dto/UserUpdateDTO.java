package com.openclassrooms.paymybuddy.dto;


import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Data Transfer Object used to update user account information.*
 * This DTO allows partial updates of user profile data.
 * All fields are optional but must respect validation constraints
 * when provided.
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserUpdateDTO {

    @Size(min = 3, max = 50,
            message = "Le nom d'utilisateur doit contenir entre 3 et 50 caractères")
    private String username;

    @Size(max = 50,
            message = "L'email ne doit pas dépasser 50 caractères")
    private String email;

    @Size(min = 8, max = 100,
            message = "Le mot de passe doit contenir au moins 8 caractères")
    private String password;
}
