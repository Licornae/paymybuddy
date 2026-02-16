package com.openclassrooms.paymybuddy.service;

import com.openclassrooms.paymybuddy.dto.TransactionViewDTO;
import com.openclassrooms.paymybuddy.model.AppUser;
import com.openclassrooms.paymybuddy.model.Transaction;
import com.openclassrooms.paymybuddy.repository.ConnectionRepository;
import com.openclassrooms.paymybuddy.repository.TransactionRepository;
import com.openclassrooms.paymybuddy.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Service layer responsible for handling money transfer transactions between users.
 * This service centralizes all business rules related to transactions, including:
 * - Validation of transaction amount
 * - Validation of sender and receiver existence
 * - Prevention of self-transfers
 * - Verification of friendship (connection) between users
 * - Creation and persistence of transactions
 * All operations are executed within a transactional context.
 * All validations are performed before persisting data to ensure data integrity
 * and business consistency.
 */
@Transactional
@Service
@Slf4j
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;
    private final ConnectionRepository connectionRepository;

    /**
     * Constructor-based dependency injection.
     *
     * @param transactionRepository repository for transaction persistence
     * @param userRepository repository for user retrieval
     * @param connectionRepository repository for connection validation
     */
    public TransactionService(TransactionRepository transactionRepository,
                              UserRepository userRepository,
                              ConnectionRepository connectionRepository) {
        this.transactionRepository = transactionRepository;
        this.userRepository = userRepository;
        this.connectionRepository = connectionRepository;
    }

    /**
     * Creates and persists a new transaction between two connected users.
     *
     * @param senderId    unique identifier of the sender
     * @param receiverId  unique identifier of the receiver
     * @param amount      amount to transfer (must be strictly positive)
     * @param description optional description (max 200 characters)
     *
     * @return persisted {@link Transaction}
     *
     * @throws IllegalArgumentException if input validation fails
     * @throws IllegalStateException if business constraints are violated
     */
    public Transaction createTransaction(int senderId,
                                         int receiverId,
                                         double amount,
                                         String description) {

        log.debug("Attempting to create transaction: senderId={}, receiverId={}, amount={}", senderId, receiverId, amount);

        validateAmount(amount);
        validateDescription(description);
        validateDifferentUsers(senderId, receiverId);

        AppUser sender = findUserById(senderId, "Expéditeur introuvable");
        AppUser receiver = findUserById(receiverId, "Récepteur introuvable");

        validateConnection(sender, receiver);

        Transaction transaction = new Transaction();
        transaction.setSender(sender);
        transaction.setReceiver(receiver);
        transaction.setAmount(amount);
        transaction.setDescription(description);
        transaction.setDateHeure(LocalDateTime.now());

        Transaction savedTransaction = transactionRepository.save(transaction);

        log.info("Transaction successfully created: transactionId={}, senderId={}, receiverId={}, amount={}",
                savedTransaction.getIdTransaction(), senderId, receiverId, amount);

        return savedTransaction;
    }

    /**
     * Validates transaction amount.
     */
    private void validateAmount(double amount) {
        if (amount <= 0) {
            log.warn("Transaction rejected: invalid amount");
            throw new IllegalArgumentException("Le montant doit être supérieur à 0€");
        }
    }

    /**
     * Validates description length.
     */
    private void validateDescription(String description) {
        if (description != null && description.length() > 200) {
            log.warn("Transaction rejected: description too long");
            throw new IllegalArgumentException("La description ne doit pas dépasser 200 caractères");
        }
    }

    /**
     * Ensures sender and receiver are different users.
     */
    private void validateDifferentUsers(int senderId, int receiverId) {
        if (senderId == receiverId) {
            log.warn("Transaction rejected: self-transfer attempt (userId={})", senderId);
            throw new IllegalArgumentException("Vous ne pouvez pas envoyer de l'argent à vous-même");
        }
    }

    /**
     * Retrieves a user by ID.
     */
    private AppUser findUserById(int userId, String errorMessage) {
        return userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.warn("User not found (id={})", userId);
                    return new IllegalArgumentException(errorMessage);
                });
    }

    /**
     * Verifies that two users are connected.
     */
    private void validateConnection(AppUser sender, AppUser receiver) {
        if (!connectionRepository.existsByUserAndFriend(sender, receiver)) {

            log.warn("Transaction rejected: users not connected (senderId={}, receiverId={})", sender.getIdUser(), receiver.getIdUser());

            throw new IllegalStateException("Les utilisateurs ne sont pas amis");
        }
    }

    /**
     * Retrieves all transactions sent by a user.
     *
     * @param email user email
     * @return list of sent transactions
     */
    public List<Transaction> getUserSentTransactions(String email) {
        log.debug("Fetching sent transactions for userEmail={}", email);

        AppUser user = findUserByEmail(email);

        List<Transaction> transactions = transactionRepository.findTransactionsBySender(user);

        log.debug("Found {} sent transactions for userId={}", transactions.size(), user.getIdUser());

        return transactions;
    }

    /**
     * Finds a user by email or throws exception.
     */
    private AppUser findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.error("User not found by email: {}", email);
                    return new IllegalStateException("Utilisateur introuvable");
                });
    }

    /**
     * Retrieves user by email.
     */
    public AppUser getUserByEmail(String email) {
        return findUserByEmail(email);
    }

    /**
     * Retrieves sent transactions mapped to DTO.
     */
    public List<TransactionViewDTO> getUserSentTransactionsDto(String email) {

        log.debug("Fetching sent transactions DTO for userEmail={}", email);

        AppUser user = findUserByEmail(email);

        return transactionRepository.findTransactionsBySender(user)
                .stream()
                .map(tr -> new TransactionViewDTO(
                        tr.getReceiver().getUsername(),
                        tr.getDescription(),
                        tr.getAmount()
                ))
                .toList();
    }
}
