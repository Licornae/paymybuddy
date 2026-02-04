package com.openclassrooms.paymybuddy.repository;

import com.openclassrooms.paymybuddy.model.AppUser;
import com.openclassrooms.paymybuddy.model.Transaction;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


/**
 * Repository interface for {@link Transaction} entities.
 *
 * This repository provides basic CRUD operations for managing transactions
 * in the database.
 *
 * Spring Data JPA automatically generates the implementation at runtime.
 */
@Repository
public interface TransactionRepository extends CrudRepository<Transaction, Integer> {
    List<Transaction> findTransactionsBySender(AppUser sender);
}
