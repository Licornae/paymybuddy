package com.openclassrooms.paymybuddy.dto;


/**
 * Data Transfer Object representing a user's connection.
 * This DTO is used to expose limited information about a connected user,
 * typically when displaying a user's contact list.
 *
 * @param id       the unique identifier of the connected user
 * @param username the username of the connected user
 */
public record ConnectionDTO(
            int id,
            String username
    ) {}
