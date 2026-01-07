package com.openclassrooms.paymybuddy.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.DynamicUpdate;

/**
 * Entity representing an application user.
 *
 * This class is mapped to the {@code app_user} table and contains
 * authentication and identification information for a user.
 */
@DynamicUpdate
@Data
@Entity
@Table(name = "app_user")
public class AppUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_user")
    private int idUser;

    @Column(name = "username", nullable = false, unique = true)
    private String username;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "email", nullable = false,  unique = true)
    private String email;

    @Column(name = "role", nullable = false)
    private String role;
}
