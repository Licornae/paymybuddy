package com.openclassrooms.paymybuddy.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.DynamicUpdate;

import java.time.LocalDateTime;

/**
 * Entity representing a financial transaction between two users.
 *
 * A transaction records the transfer of an amount from a sender to a receiver
 * at a specific date and time, with an optional description.
 *
 * This entity is mapped to the {@code transaction} table in the database.
 */
@DynamicUpdate
@Data
@Entity
@Table(name = "transaction")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_transaction")
    private int idTransaction;

    @Column(name = "description", length = 200)
    private String description;

    @Column(name = "date_heure", nullable = false)
    private LocalDateTime dateHeure;

    @Column(name = "amount", nullable = false)
    private Double amount;

    @ManyToOne(optional = false)
    @JoinColumn(name = "sender_id")
    private AppUser sender;

    @ManyToOne(optional = false)
    @JoinColumn(name = "receiver_id")
    private AppUser receiver;
}
