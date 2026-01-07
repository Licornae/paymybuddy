package com.openclassrooms.paymybuddy.repositoryTest;

import com.openclassrooms.paymybuddy.model.AppUser;
import com.openclassrooms.paymybuddy.model.Transaction;
import com.openclassrooms.paymybuddy.repository.TransactionRepository;
import com.openclassrooms.paymybuddy.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
public class TransactionRepositoryTest {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    public void shouldSaveTransactionBetweenTwoUsers() {

        AppUser sender = new AppUser();
        sender.setUsername("sender");
        sender.setEmail("sender@mail.com");
        sender.setPassword("password");
        sender.setRole("USER");

        AppUser receiver = new AppUser();
        receiver.setUsername("receiver");
        receiver.setEmail("receiver@mail.com");
        receiver.setPassword("password");
        receiver.setRole("USER");

        userRepository.save(sender);
        userRepository.save(receiver);

        Transaction transaction = new Transaction();
        transaction.setSender(sender);
        transaction.setReceiver(receiver);
        transaction.setAmount(100.0);
        transaction.setDescription("Refund");
        transaction.setDateHeure(LocalDateTime.now());

        Transaction saved = transactionRepository.save(transaction);

        assertThat(saved.getIdTransaction()).isNotNull();
        assertThat(saved.getAmount()).isEqualTo(100.0);
    }

    @Test
    public void shouldNotSaveTransactionWithoutSender() {

        AppUser receiver = new AppUser();
        receiver.setUsername("receiver");
        receiver.setEmail("receiver@mail.com");
        receiver.setPassword("password");
        receiver.setRole("USER");

        userRepository.save(receiver);

        Transaction transaction = new Transaction();
        transaction.setReceiver(receiver);
        transaction.setAmount(100.0);
        transaction.setDescription("Refund");
        transaction.setDateHeure(LocalDateTime.now());

        assertThatThrownBy(() -> transactionRepository.save(transaction))
                .isInstanceOf(Exception.class);
    }

    @Test
    public void shouldNotSaveTransactionWithoutReceiver() {

        AppUser sender = new AppUser();
        sender.setUsername("sender");
        sender.setEmail("sender@mail.com");
        sender.setPassword("password");
        sender.setRole("USER");

        userRepository.save(sender);

        Transaction transaction = new Transaction();
        transaction.setSender(sender);
        transaction.setAmount(100.0);
        transaction.setDescription("Refund");
        transaction.setDateHeure(LocalDateTime.now());

        assertThatThrownBy(() -> transactionRepository.save(transaction))
                .isInstanceOf(Exception.class);
    }

}