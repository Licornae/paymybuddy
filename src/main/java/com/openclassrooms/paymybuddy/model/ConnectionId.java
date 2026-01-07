package com.openclassrooms.paymybuddy.model;

import java.io.Serializable;
import java.util.Objects;

/**
 * Composite primary key class for the {@link Connection} entity.
 *
 * This class represents the unique identity of a connection between two users.
 * The identity is defined by the combination of:
 * - the identifier of the user
 * - the identifier of the connected friend
 *
 * It is used by JPA through the {@code @IdClass} mechanism to map
 * a composite primary key based on foreign keys.
 *
 * This class complies with JPA requirements:
 * - implements {@link Serializable}
 * - provides a no-argument constructor
 * - overrides {@link #equals(Object)} and {@link #hashCode()}
 */
public class ConnectionId implements Serializable {

    private Integer user;
    private Integer friend;

    public ConnectionId() {}

    public ConnectionId(Integer user, Integer friend) {
        this.user = user;
        this.friend = friend;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ConnectionId)) return false;
        ConnectionId that = (ConnectionId) o;
        return Objects.equals(user, that.user)
                && Objects.equals(friend, that.friend);
    }

    @Override
    public int hashCode() {
        return Objects.hash(user, friend);
    }
}