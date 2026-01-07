package com.openclassrooms.paymybuddy.service;

import com.openclassrooms.paymybuddy.model.AppUser;
import com.openclassrooms.paymybuddy.model.Transaction;
import com.openclassrooms.paymybuddy.repository.ConnectionRepository;
import com.openclassrooms.paymybuddy.repository.TransactionRepository;
import com.openclassrooms.paymybuddy.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * Service layer responsible for handling money transfer transactions between users.
 *
 * This service centralizes all business rules related to transactions, including:
 * - Validation of transaction amount
 * - Validation of sender and receiver existence
 * - Prevention of self-transfers
 * - Verification of friendship (connection) between users
 * - Creation and persistence of transactions
 *
 * All validations are performed before persisting data to ensure data integrity
 * and business consistency.
 */
@Transactional
@Service
public class TransactionService {

    @Autowired
    TransactionRepository transactionRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ConnectionRepository connectionRepository;

    /**
     * Creates and persists a new transaction between two connected users.
     *
     * @param senderId    the unique identifier of the user sending the money
     * @param receiverId  the unique identifier of the user receiving the money
     * @param amount      the amount of money to transfer
     * @param description a textual description of the transaction
     *
     * @return the persisted {@link Transaction} entity
     *
     * @throws IllegalArgumentException if the amount is not valid, if users are identical,
     *                                  or if one of the users does not exist
     * @throws IllegalStateException    if the users are not connected as friends
     */
    public Transaction createTransaction(int senderId,
                                         int receiverId,
                                         double amount,
                                         String description) {

        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be greater than zero");
        }

        if (senderId == receiverId) {
            throw new IllegalArgumentException("Sender and receiver must be different");
        }

        AppUser sender = userRepository.findById(senderId)
                .orElseThrow(() -> new IllegalArgumentException("Sender not found"));

        AppUser receiver = userRepository.findById(receiverId)
                .orElseThrow(() -> new IllegalArgumentException("Receiver not found"));

        if (!connectionRepository.existsByUserAndFriend(sender, receiver)) {
            throw new IllegalStateException("Users are not friends");
        }

        Transaction transaction = new Transaction();
        transaction.setSender(sender);
        transaction.setReceiver(receiver);
        transaction.setAmount(amount);
        transaction.setDescription(description);
        transaction.setDateHeure(LocalDateTime.now());

        return transactionRepository.save(transaction);
    }
}
