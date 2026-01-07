package com.openclassrooms.paymybuddy.repository;

import com.openclassrooms.paymybuddy.model.AppUser;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for {@link AppUser} persistence operations.
 *
 * Provides CRUD operations and custom queries to retrieve users
 * by email or username.
 */
@Repository
public interface UserRepository extends CrudRepository<AppUser,Integer> {

    /**
     * Finds a user by email.
     *
     * @param email the email to search for
     * @return the matching user, or {@code null} if none found
     */
    AppUser findByEmail(String email);

    /**
     * Finds a user by username.
     *
     * @param username the username to search for
     * @return the matching user, or {@code null} if none found
     */
    AppUser findByUsername(String username);
}
