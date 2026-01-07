package com.openclassrooms.paymybuddy.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import com.openclassrooms.paymybuddy.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import com.openclassrooms.paymybuddy.model.AppUser;

/**
 * Service layer handling business logic related to users.
 *
 * Ensures uniqueness of email and username before saving a user.
 */
@Slf4j
@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    /**
     * Saves a new user after validating email and username uniqueness.
     *
     * @param user the user to save
     * @return the saved user
     * @throws IllegalArgumentException if email or username already exists
     */
    public AppUser saveUser(AppUser user) {

        log.info("Attempting to save user with email={} and username={}",
                user.getEmail(), user.getUsername());

        if (userRepository.findByEmail(user.getEmail()) != null){
            log.warn("Email already exists: {}", user.getEmail());
            throw new IllegalArgumentException("Email already exists");
        }

        if (userRepository.findByUsername(user.getUsername()) != null){
            log.warn("Username already exists: {}", user.getUsername());
            throw new IllegalArgumentException("Username already exists");
        }

        AppUser savedUser = userRepository.save(user);
        log.info("User successfully saved with id={}", savedUser.getIdUser());

        return savedUser;
    }

}
