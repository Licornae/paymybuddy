package com.openclassrooms.paymybuddy.service;

import com.openclassrooms.paymybuddy.model.AppUser;
import com.openclassrooms.paymybuddy.model.Connection;
import com.openclassrooms.paymybuddy.repository.ConnectionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Service for managing user connections.
 *
 * This service handles business rules related to user connections, such as:
 * - Preventing a user from adding themselves as a friend
 * - Preventing duplicate connections
 * - Persisting valid connections
 *
 * All validations are performed before accessing the persistence layer.
 */
@Slf4j
@Service
public class ConnectionService {

    @Autowired
    ConnectionRepository connectionRepository;

    /**
     * Creates a new connection between two users.
     *
     * @param user   the user who adds a friend
     * @param friend the user to be added as a friend
     *
     * @throws IllegalArgumentException if the user tries to add themselves
     * or if the connection already exists
     */
    public void addConnection(AppUser user, AppUser friend) {

        if (user.equals(friend)) {
            throw new IllegalArgumentException("Cannot add yourself as a friend");
        }

        if (connectionRepository.existsByUserAndFriend(user, friend)) {
            throw new IllegalArgumentException("Connection already exists");
        }

        Connection connection = new Connection();
        connection.setUser(user);
        connection.setFriend(friend);

        connectionRepository.save(connection);
    }
}
