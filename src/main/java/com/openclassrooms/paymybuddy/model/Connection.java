package com.openclassrooms.paymybuddy.model;

import jakarta.persistence.*;
import lombok.Data;

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
