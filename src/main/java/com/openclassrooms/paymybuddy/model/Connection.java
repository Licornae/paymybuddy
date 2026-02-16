package com.openclassrooms.paymybuddy.model;

import jakarta.persistence.*;
import lombok.Data;

/**
 * Entity representing a connection (friendship) between two users.
 * A {@code Connection} models a relationship between a user and one of
 * their connected friends.
 * This entity uses a composite primary key defined by the combination
 * of the {@code user} and {@code friend} associations.
 * The composite key is mapped using the {@link ConnectionId} class
 * through the {@code @IdClass} mechanism.
 * Each connection is uniquely identified by the pair
 * {@code (user, friend)}, preventing duplicate connections
 * between the same users.
 */
@Entity
@Table(name = "connection")
@IdClass(ConnectionId.class)
@Data
public class Connection {

    @Id
    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id_user")
    private AppUser user;

    @Id
    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id_user1")
    private AppUser friend;
}
