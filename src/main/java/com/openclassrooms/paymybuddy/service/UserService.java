package com.openclassrooms.paymybuddy.service;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import com.openclassrooms.paymybuddy.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import com.openclassrooms.paymybuddy.model.AppUser;

/**
 * Service layer handling business logic related to users.*
 * Ensures uniqueness of email and username before saving a user.
 */
@Slf4j
@Service
@Transactional //Le service est transactionnel afin de garantir l’atomicité des règles métier avant la persistance.
public class UserService {

    private static final int MAX_USERNAME_LENGTH = 50;
    private static final int MAX_EMAIL_LENGTH = 50;
    private static final int MAX_PASSWORD_LENGTH = 250;

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository,
                       BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Registers a new user.
     *
     * @param username user's username
     * @param email    user's email
     * @param password raw password
     * @return saved AppUser
     */
    public AppUser registerUser(String username, String email, String password) {

        validateUserData(username, email, password);
        checkUniqueness(username, email);

        AppUser user = new AppUser();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole("USER");

        log.info("Registering new user with email={}", email);
        return userRepository.save(user);
    }

    private void validateUserData(String username, String email, String password) {

        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Le nom d'utilisateur est obligatoire");
        }
        if (username.length() > MAX_USERNAME_LENGTH) {
            throw new IllegalArgumentException("Le nom d'utilisateur ne doit pas dépasser 50 caractères");
        }

        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("L'email est obligatoire");
        }
        if (email.length() > MAX_EMAIL_LENGTH) {
            throw new IllegalArgumentException("L'email ne doit pas dépasser 50 caractères");
        }

        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException("Le mot de passe est obligatoire");
        }

        if (password.length() > MAX_PASSWORD_LENGTH) {
            throw new IllegalArgumentException("Le mot de passe ne doit pas dépasser 250 caractères");
        }
    }

    private void checkUniqueness(String username, String email) {

        if (userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("Ce nom d'utilisateur existe déjà");
        }

        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("L'email existe déjà");
        }
    }
}