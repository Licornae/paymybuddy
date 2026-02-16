package com.openclassrooms.paymybuddy.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Configuration class for Spring Security.
 * This class defines the security rules for the PayMyBuddy application.
 * It configures:
 * - Password encoding mechanism
 * - Authentication rules
 * - Authorization rules
 * - Custom login configuration
 * - Logout behavior
 * Security rules:
 * - Public access: "/", "/register", "/login", "/css/**"
 * - ll other requests require authentication
 * Authentication is handled using a custom login page.
 */
@Configuration
@EnableWebSecurity
public class SpringSecurityConfig {

    /**
     * Defines the password encoder bean used to hash user passwords.
     * BCrypt is used as it provides strong hashing with built-in salt
     * and adaptive strength to protect against brute-force attacks.
     *
     * @return a BCryptPasswordEncoder instance
     */
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Configures the security filter chain.
     * This method defines:
     * - CSRF configuration
     * - Authorization rules
     * - Form-based authentication settings
     * - Logout behavior
     *
     * @param http the {@link HttpSecurity} object used to configure web security
     * @return the configured {@link SecurityFilterChain}
     * @throws Exception if a security configuration error occurs
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        return http
                .csrf(AbstractHttpConfigurer::disable)

                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/","/register", "/login", "/css/**").permitAll()
                        .anyRequest().authenticated())

                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/login")
                        .defaultSuccessUrl("/transfert", true)
                        .failureUrl("/login?error")
                        .permitAll())

                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login"))

                .build();
    }
}
