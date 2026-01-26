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

    /**
     * Adds a new connection using the friend's email address.
     *
     * This is the only method intended to be used by the controller layer.
     * It retrieves the target user by email and delegates the creation
     * of the connection to {@link #addConnection(AppUser, AppUser)}.
     *
     * @param currentUserEmail the authenticated user who adds a connection
     * @param friendEmail the email address of the user to be added
     * @throws IllegalArgumentException if no user is found with the given email
     */
    public void addConnectionByEmail(String currentUserEmail, String friendEmail) {

        //Charger l'utilisateur connecté DEPUIS LA BASE
        AppUser user = userRepository.findByEmail(currentUserEmail)
                .orElseThrow(() ->
                        new IllegalStateException("Utilisateur connecté introuvable"));

        log.debug("Attempting to add connection by email: userId={}, email={}",
                user.getIdUser(), friendEmail);

        //Charger l'ami depuis la base
        AppUser friend = userRepository.findByEmail(friendEmail)
                .orElseThrow(() ->
                        new IllegalArgumentException("Cet utilisateur n'est pas sur l'application"));

        if (user.equals(friend)) {
            throw new IllegalArgumentException("Vous ne pouvez pas vous ajouter vous-même");
        }

        if (connectionRepository.existsByUserAndFriend(user, friend)) {
            throw new IllegalArgumentException("Cette relation existe déjà");
        }

        //Création de la relation
        Connection connection = new Connection();
        connection.setUser(user);
        connection.setFriend(friend);

        connectionRepository.save(connection);
    }
}
