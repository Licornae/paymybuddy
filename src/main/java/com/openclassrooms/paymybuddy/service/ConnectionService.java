package com.openclassrooms.paymybuddy.service;

import com.openclassrooms.paymybuddy.model.AppUser;
import com.openclassrooms.paymybuddy.model.Connection;
import com.openclassrooms.paymybuddy.repository.ConnectionRepository;
import com.openclassrooms.paymybuddy.repository.UserRepository;
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

    @Autowired
    UserRepository userRepository;

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

        log.debug("Attempting to add connection: user={} friend={}",
                user.getIdUser(), friend.getIdUser());

        if (user.equals(friend)) {
            log.warn("User {} attempted to add themselves as a friend",
                    user.getIdUser());
            throw new IllegalArgumentException("Cannot add yourself as a friend");
        }

        if (connectionRepository.existsByUserAndFriend(user, friend)) {
            log.warn("Duplicate connection attempt: user={} friend={}",
                    user.getIdUser(), friend.getIdUser());
            throw new IllegalArgumentException("Connection already exists");
        }

        Connection connection = new Connection();
        connection.setUser(user);
        connection.setFriend(friend);

        connectionRepository.save(connection);
        log.info("Connection successfully created: user={} friend={}",
                user.getIdUser(), friend.getIdUser());
    }


    public void addConnectionByEmail(AppUser user, String friendEmail) {

        AppUser friend = userRepository.findByEmail(friendEmail);

        if (friend == null) {
            throw new IllegalArgumentException("User not found");
        }

        addConnection(user, friend);
    }
}
