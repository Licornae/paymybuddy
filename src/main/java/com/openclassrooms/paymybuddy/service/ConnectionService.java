package com.openclassrooms.paymybuddy.service;

import com.openclassrooms.paymybuddy.dto.ConnectionDTO;
import com.openclassrooms.paymybuddy.model.AppUser;
import com.openclassrooms.paymybuddy.model.Connection;
import com.openclassrooms.paymybuddy.repository.ConnectionRepository;
import com.openclassrooms.paymybuddy.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

/**
 * Service for managing user connections.
 * This service handles business rules related to user connections, such as:
 * - Prevents a user from adding themselves
 * - Prevents duplicate connections
 * - Handles user lookup by email
 * - Maps connection entities to DTOs
 * All operations are executed within a transactional context.
 */
@Slf4j
@Service
@Transactional
public class ConnectionService {

    private final ConnectionRepository connectionRepository;
    private final UserRepository userRepository;

    /**
     * Constructs a ConnectionService with required repositories.
     *
     * @param connectionRepository repository used to manage Connection entities
     * @param userRepository repository used to manage AppUser entities
     */
    public ConnectionService(ConnectionRepository connectionRepository,
                             UserRepository userRepository) {
        this.connectionRepository = connectionRepository;
        this.userRepository = userRepository;
    }

    /**
     * Creates a new connection between two users.
     * Business validations performed:
     * - User cannot add themselves
     * - Connection must not already exist
     *
     * @param user   the user initiating the connection
     * @param friend the user to be added as a connection
     *
     * @throws IllegalArgumentException if:
     * - the user attempts to add themselves
     * - the connection already exists
     */
    public void addConnection(AppUser user, AppUser friend) {

        log.debug("Attempting to add connection: user={} friend={}",
                user.getIdUser(), friend.getIdUser());

        if (Objects.equals(user.getIdUser(), friend.getIdUser())) {
            log.warn("User {} attempted to add themselves as a friend", user.getIdUser());
            throw new IllegalArgumentException("Vous ne pouvez pas vous ajouter vous-même");
        }

        if (connectionRepository.existsByUserAndFriend(user, friend)) {
            log.warn("Duplicate connection attempt: user={} friend={}", user.getIdUser(), friend.getIdUser());
            throw new IllegalArgumentException("La connection existe déjà");
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
     * This is the only method intended to be used by the controller layer.
     * It retrieves the target user by email and delegates the creation
     * of the connection to {@link #addConnection(AppUser, AppUser)}.
     *
     * @param currentUserEmail the authenticated user who adds a connection
     * @param friendEmail the email address of the user to be added
     */
    public void addConnectionByEmail(String currentUserEmail, String friendEmail) {

        log.debug("addConnectionByEmail called: currentUserEmail={} friendEmail={}", currentUserEmail, friendEmail);

        AppUser user = findUserByEmail(currentUserEmail, true);
        AppUser friend = findUserByEmail(friendEmail, false);

        addConnection(user, friend);
    }

    /**
     * Retrieves a user by email address.
     *
     * @param email the email address to search for
     * @param isCurrentUser indicates whether the email belongs to the authenticated user
     *
     * @return the matching AppUser entity
     *
     * @throws IllegalStateException if the authenticated user is not found
     * @throws IllegalArgumentException if a non-authenticated user is not found
     */
    private AppUser findUserByEmail(String email, boolean isCurrentUser) {
        log.debug("Searching user by email: {}", email);

        return userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    if (isCurrentUser) {
                        log.error("Authenticated user not found in database: {}", email);
                        return new IllegalStateException("Utilisateur connecté introuvable");
                    } else {
                        log.warn("User not found for connection attempt: {}", email);
                        return new IllegalArgumentException("Cet utilisateur n'est pas sur l'application");
                    }
                });
    }

    /**
     * Retrieves all connections of a given user and converts them into DTOs.
     *
     * @param email the email of the user whose connections must be retrieved
     *
     * @return a list of {@link ConnectionDTO} representing the user's connections
     *
     * @throws IllegalStateException if the user cannot be found
     */
    public List<ConnectionDTO> getUserConnectionsDto(String email) {

        log.debug("Fetching connections for user email={}", email);

        AppUser user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.error("User not found while fetching connections: {}", email);
                    return new IllegalStateException("Utilisateur introuvable");
                });

        List<ConnectionDTO> connections = connectionRepository.findConnectionsByUser(user)
                .stream()
                .map(c -> new ConnectionDTO(
                        c.getFriend().getIdUser(),
                        c.getFriend().getUsername()
                ))
                .toList();

        log.debug("Found {} connections for userId={}", connections.size(), user.getIdUser());

        return connections;
    }

}
