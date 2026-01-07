package com.openclassrooms.paymybuddy.repository;

import com.openclassrooms.paymybuddy.model.AppUser;
import com.openclassrooms.paymybuddy.model.Connection;
import com.openclassrooms.paymybuddy.model.ConnectionId;
import org.springframework.data.repository.CrudRepository;

/**
 * Repository interface for managing {@link Connection} entities.
 *
 * This repository provides basic CRUD operations for {@code Connection}
 * entities identified by a composite primary key represented by {@link ConnectionId}.
 *
 * The composite key is based on the identifiers of the
 * {@code user} and the {@code friend}.
 */
public interface ConnectionRepository extends CrudRepository<Connection, ConnectionId> {

    boolean existsByUserAndFriend(AppUser user, AppUser friend);
}
