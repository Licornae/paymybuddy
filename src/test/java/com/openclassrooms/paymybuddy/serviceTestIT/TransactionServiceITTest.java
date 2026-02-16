package com.openclassrooms.paymybuddy.serviceTestIT;

import com.openclassrooms.paymybuddy.dto.TransactionViewDTO;
import com.openclassrooms.paymybuddy.model.AppUser;
import com.openclassrooms.paymybuddy.model.Connection;
import com.openclassrooms.paymybuddy.model.Transaction;
import com.openclassrooms.paymybuddy.repository.ConnectionRepository;
import com.openclassrooms.paymybuddy.repository.TransactionRepository;
import com.openclassrooms.paymybuddy.repository.UserRepository;
import com.openclassrooms.paymybuddy.service.TransactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class TransactionServiceITTest {

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ConnectionRepository connectionRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    private AppUser sender;
    private AppUser receiver;

    @BeforeEach
    public void setup() {
        sender = new AppUser();
        sender.setUsername("sender");
        sender.setEmail("sender@mail.com");
        sender.setPassword("pass");
        sender.setRole("USER");
        sender = userRepository.save(sender);

        receiver = new AppUser();
        receiver.setUsername("receiver");
        receiver.setEmail("receiver@mail.com");
        receiver.setPassword("pass");
        receiver.setRole("USER");
        receiver = userRepository.save(receiver);

        Connection connection = new Connection();
        connection.setUser(sender);
        connection.setFriend(receiver);
        connectionRepository.save(connection);
    }

    @Test
    public void shouldCreateTransactionSuccessfully() {

        Transaction transaction = transactionService.createTransaction(
                sender.getIdUser(),
                receiver.getIdUser(),
                50.0,
                "Resto"
        );

        assertThat(transactionRepository.count()).isEqualTo(1);
        assertThat(transaction.getAmount()).isEqualTo(50.0);
        assertThat(transaction.getSender().getUsername()).isEqualTo("sender");
    }

    @Test
    public void shouldThrowExceptionWhenAmountInvalid() {

        Throwable thrown = catchThrowable(() ->
                transactionService.createTransaction(
                        sender.getIdUser(),
                        receiver.getIdUser(),
                        0,
                        "Resto"
                )
        );

        assertThat(thrown).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Le montant doit être supérieur à 0€");

        assertThat(transactionRepository.count()).isZero();
    }

    @Test
    public void shouldThrowExceptionWhenSelfTransfer() {

        Throwable thrown = catchThrowable(() ->
                transactionService.createTransaction(
                        sender.getIdUser(),
                        sender.getIdUser(),
                        50,
                        "Resto"
                )
        );

        assertThat(thrown).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Vous ne pouvez pas envoyer de l'argent à vous-même");
    }

    @Test
    public void shouldThrowExceptionWhenUsersNotConnected() {

        AppUser stranger = new AppUser();
        stranger.setUsername("stranger");
        stranger.setEmail("stranger@mail.com");
        stranger.setPassword("pass");
        stranger.setRole("USER");

        AppUser savedStranger = userRepository.save(stranger);

        Throwable thrown = catchThrowable(() ->
                transactionService.createTransaction(
                        sender.getIdUser(),
                        savedStranger.getIdUser(),
                        50,
                        "Resto"
                )
        );

        assertThat(thrown).isInstanceOf(IllegalStateException.class)
                .hasMessage("Les utilisateurs ne sont pas amis");
    }

    @Test
    public void shouldThrowExceptionWhenUserNotFound() {

        Throwable thrown = catchThrowable(() ->
                transactionService.createTransaction(
                        999,
                        receiver.getIdUser(),
                        50,
                        "Resto"
                )
        );

        assertThat(thrown).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Expéditeur introuvable");
    }

    @Test
    public void shouldReturnUserSentTransactions() {

        transactionService.createTransaction(
                sender.getIdUser(),
                receiver.getIdUser(),
                50,
                "Resto"
        );

        List<Transaction> transactions =
                transactionService.getUserSentTransactions("sender@mail.com");

        assertThat(transactions).hasSize(1);
        assertThat(transactions.getFirst().getAmount()).isEqualTo(50);
    }

    @Test
    public void shouldReturnUserSentTransactionsDto() {

        transactionService.createTransaction(
                sender.getIdUser(),
                receiver.getIdUser(),
                50,
                "Resto"
        );

        List<TransactionViewDTO> result =
                transactionService.getUserSentTransactionsDto("sender@mail.com");

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().receiver()).isEqualTo("receiver");
        assertThat(result.getFirst().amount()).isEqualTo(50);
    }

    @Test
    public void shouldThrowExceptionWhenEmailUnknown() {

        Throwable thrown = catchThrowable(() ->
                transactionService.getUserSentTransactionsDto("unknown@mail.com")
        );

        assertThat(thrown)
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Utilisateur introuvable");
    }
}
