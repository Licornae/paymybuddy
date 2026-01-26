package com.openclassrooms.paymybuddy.configuration;

import com.openclassrooms.paymybuddy.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import com.openclassrooms.paymybuddy.model.AppUser;

import java.util.List;

/**
 * Custom implementation of {@link UserDetailsService} used by Spring Security
 * to load user-specific data during the authentication process.
 *
 * This service retrieves a user from the database using their email address,
 * then adapts the {@link AppUser} entity into a Spring Security {@link UserDetails}
 * object.
 *
 * The user's role is automatically prefixed with {@code ROLE_} to comply with
 * Spring Security conventions.
 */
@Service
@Slf4j
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    UserRepository userRepository;

    /**
     * Loads a user by their email address.
     *
     * This method is automatically called by Spring Security during authentication.
     *
     * @param email the email address used as the username
     * @return a fully populated {@link UserDetails} object
     * @throws UsernameNotFoundException if no user is found with the given email
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        log.debug("Attempting to authenticate user with email={}", email);

        AppUser user = userRepository.findByEmail(email).orElseThrow(() -> {
            log.warn("Authentication failed: user not found with email={}", email);
            return new UsernameNotFoundException("User not found");
        });

        log.info("User authenticated successfully with email={}", email);

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole()))
        );
    }
}
