package com.openclassrooms.paymybuddy.service;

import com.openclassrooms.paymybuddy.model.AppUser;
import com.openclassrooms.paymybuddy.model.Transaction;
import com.openclassrooms.paymybuddy.repository.ConnectionRepository;
import com.openclassrooms.paymybuddy.repository.TransactionRepository;
import com.openclassrooms.paymybuddy.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class TransactionService {

    @Autowired
    TransactionRepository transactionRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ConnectionRepository connectionRepository;

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
