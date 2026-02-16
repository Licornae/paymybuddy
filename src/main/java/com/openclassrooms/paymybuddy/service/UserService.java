package com.openclassrooms.paymybuddy.service;

import com.openclassrooms.paymybuddy.dto.UserUpdateDTO;
import com.openclassrooms.paymybuddy.repository.ConnectionRepository;
import com.openclassrooms.paymybuddy.repository.TransactionRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import com.openclassrooms.paymybuddy.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import com.openclassrooms.paymybuddy.model.AppUser;

/**
 * Service class for managing user operations in the PayMyBuddy application.
 * Handles user registration, updates, retrieval, and deletion with validation
 * and security features including password encryption and uniqueness checks.
 */
@Service
@Slf4j
@Transactional
public class UserService {

    private static final int MAX_USERNAME_LENGTH = 50;
    private static final int MAX_EMAIL_LENGTH = 50;
    private static final int MAX_PASSWORD_LENGTH = 250;
    private static final String USER_NOT_FOUND = "Utilisateur introuvable";

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final ConnectionRepository connectionRepository;
    private final TransactionRepository transactionRepository;


    /**
     * Constructs a UserService with required dependencies.
     *
     * @param userRepository the user repository for database operations
     * @param passwordEncoder the BCrypt password encoder for secure password storage
     * @param connectionRepository the connection repository for managing user connections
     * @param transactionRepository the transaction repository for managing user transactions
     */
    @Autowired
    public UserService(UserRepository userRepository,
                       BCryptPasswordEncoder passwordEncoder, ConnectionRepository connectionRepository, TransactionRepository transactionRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.connectionRepository = connectionRepository;
        this.transactionRepository = transactionRepository;
    }

    /**
     * Registers a new user with the provided credentials.
     *
     * @param username the desired username
     * @param email    the user's email address
     * @param password the user's password (will be encrypted)
     * @throws IllegalArgumentException if validation fails or username/email already exists
     */
    public void registerUser(String username, String email, String password) {

        validateUserData(username, email, password);
        checkUniqueness(username, email);

        AppUser user = new AppUser();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole("USER");

        log.info("Registering new user with email={}", email);

        userRepository.save(user);
    }

    /**
     * Retrieves a user by their email address.
     *
     * @param email the email to search for
     * @return the AppUser with the specified email
     * @throws IllegalArgumentException if no user is found with the given email
     */
    public AppUser getUserByEmail(String email) {
        log.debug("Fetching user by email={}", email);
        return userRepository.findByEmail(email)
                .orElseThrow(() -> {
                log.warn("User not found with email={}", email);
                return new IllegalArgumentException(USER_NOT_FOUND);
            });
    }

    /**
     * Updates an existing user's profile information.
     * Allows partial updates of username, email, and password. Only non-blank
     * fields in the DTO will be updated.
     *
     * @param userId the ID of the user to update
     * @param dto    the user update data transfer object containing new values
     * @throws IllegalArgumentException if user not found or validation fails
     */
    public void updateUser(int userId, UserUpdateDTO dto) {
        log.debug("Updating user id={}", userId);

        AppUser user = userRepository.findById(userId)
                .orElseThrow(() -> {
                log.warn("User not found for update, id={}", userId);
                return new IllegalArgumentException(USER_NOT_FOUND);
            });

        // USERNAME
        if (hasText(dto.getUsername())) {
            validateUsername(dto.getUsername());
            if (!dto.getUsername().equals(user.getUsername())
                    && userRepository.existsByUsername(dto.getUsername())) {
                log.warn("Username already exists: {}", dto.getUsername());
                throw new IllegalArgumentException("Ce nom d'utilisateur existe déjà");
            }
            user.setUsername(dto.getUsername());
            log.debug("Username updated to: {}", dto.getUsername());
        }

        // EMAIL
        if (hasText(dto.getEmail())) {
            validateEmail(dto.getEmail());
            if (!dto.getEmail().equals(user.getEmail())
                    && userRepository.existsByEmail(dto.getEmail())) {
                log.warn("Email already exists: {}", dto.getEmail());
                throw new IllegalArgumentException("L'email existe déjà");
            }
            user.setEmail(dto.getEmail());
            log.debug("Email updated to: {}", dto.getEmail());
        }

        // PASSWORD
        if (hasText(dto.getPassword())) {
            validatePassword(dto.getPassword());
            user.setPassword(passwordEncoder.encode(dto.getPassword()));
            log.debug("Password updated for user id={}", userId);
        }
        log.info("User id={} successfully updated", userId);
        userRepository.save(user);
    }

    private boolean hasText(String value) {
    return value != null && !value.isBlank();
    }

    /**
     * Validates the username according to business rules.
     *
     * @param username the username to validate
     * @throws IllegalArgumentException if username is blank or exceeds maximum length
     */
    private void validateUsername(String username) {
        if (username == null || username.isBlank()) {
            log.debug("Username validation failed: blank");
            throw new IllegalArgumentException("Nom d'utilisateur obligatoire");
        }
        if (username.length() > MAX_USERNAME_LENGTH) {
            log.debug("Username validation failed: exceeds max length");
            throw new IllegalArgumentException("Le nom d'utilisateur ne doit pas dépasser 50 caractères");
        }
    }

     /**
     * Validates the email address according to business rules.
     *
     * @param email the email to validate
     * @throws IllegalArgumentException if email is blank or exceeds maximum length
     */
    private void validateEmail(String email) {
        if (email == null || email.isBlank()) {
            log.debug("Email validation failed: blank");
            throw new IllegalArgumentException("Email obligatoire");
        }
        if (email.length() > MAX_EMAIL_LENGTH) {
            log.debug("Email validation failed: exceeds max length");
            throw new IllegalArgumentException("Email trop long");
        }
    }

    /**
     * Validates the password according to business rules.
     *
     * @param password the password to validate
     * @throws IllegalArgumentException if password is blank or exceeds maximum length
     */
    private void validatePassword(String password) {
        if (password == null ||password.isBlank()) {
            log.debug("Password validation failed: blank");
            throw new IllegalArgumentException("Mot de passe obligatoire");
        }
        if (password.length() < 8) {
            log.debug("Password validation failed: less than min length");
            throw new IllegalArgumentException("Mot de passe trop court");
        }

        if (password.length() > MAX_PASSWORD_LENGTH) {
            log.debug("Password validation failed: exceeds max length");
            throw new IllegalArgumentException("Mot de passe trop long");
        }
    }

    /**
     * Validates all user registration data.
     *
     * @param username the username to validate
     * @param email the email to validate
     * @param password the password to validate
     * @throws IllegalArgumentException if any field is invalid
     */
    private void validateUserData(String username, String email, String password) {
        log.trace("Validating user registration data");
        validateUsername(username);
        validateEmail(email);
        validatePassword(password);
    }

    /**
     * Checks if username and email are unique in the database.
     *
     * @param username the username to check for uniqueness
     * @param email the email to check for uniqueness
     * @throws IllegalArgumentException if username or email already exists
     */
    private void checkUniqueness(String username, String email) {
        log.trace("Checking uniqueness for username={}, email={}", username, email);

        if (userRepository.existsByUsername(username)) {
            log.warn("Username is not unique: {}", username);
            throw new IllegalArgumentException("Ce nom d'utilisateur existe déjà");
        }

        if (userRepository.existsByEmail(email)) {
            log.warn("Email is not unique: {}", email);
            throw new IllegalArgumentException("L'email existe déjà");
        }
    }

    /**
     * Deletes a user and all associated data.
     * Removes the user along with their connections and transactions
     * (both as sender and receiver).
     *
     * @param userId the ID of the user to delete
     * @throws IllegalArgumentException if user not found
     */
    @Transactional
    public void deleteUser(int userId) {

        AppUser user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException(USER_NOT_FOUND));

        log.info("Deleting user id={}", userId);

        connectionRepository.deleteByUser(user);
        connectionRepository.deleteByFriend(user);

        transactionRepository.deleteBySender(user);
        transactionRepository.deleteByReceiver(user);

        userRepository.delete(user);

        log.info("User id={} successfully deleted", userId);
    }

}