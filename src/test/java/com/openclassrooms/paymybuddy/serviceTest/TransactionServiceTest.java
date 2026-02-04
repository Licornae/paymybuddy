package com.openclassrooms.paymybuddy.serviceTest;

import com.openclassrooms.paymybuddy.model.AppUser;
import com.openclassrooms.paymybuddy.model.Transaction;
import com.openclassrooms.paymybuddy.repository.ConnectionRepository;
import com.openclassrooms.paymybuddy.repository.TransactionRepository;
import com.openclassrooms.paymybuddy.repository.UserRepository;
import com.openclassrooms.paymybuddy.service.TransactionService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.Mock;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ConnectionRepository connectionRepository;

    @InjectMocks
    private TransactionService transactionService;

    @Test
    public void shouldCreateTransactionWhenValid() {

        AppUser sender = new AppUser();
        sender.setIdUser(1);
        sender.setUsername("sender");

        AppUser receiver = new AppUser();
        receiver.setIdUser(2);
        receiver.setUsername("receiver");

        when(userRepository.findById(1)).thenReturn(Optional.of(sender));
        when(userRepository.findById(2)).thenReturn(Optional.of(receiver));
        when(connectionRepository.existsByUserAndFriend(sender, receiver))
                .thenReturn(true);

        when(transactionRepository.save(any(Transaction.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Transaction result = transactionService.createTransaction(
                1, 2, 100.0, "Refund"
        );

        assertThat(result).isNotNull();
        assertThat(result.getAmount()).isEqualTo(100.0);
        assertThat(result.getSender()).isEqualTo(sender);
        assertThat(result.getReceiver()).isEqualTo(receiver);

        verify(transactionRepository).save(any(Transaction.class));
    }

    @Test
    public void shouldThrowExceptionWhenAmountIsNegative() {

        Throwable thrown = catchThrowable(() ->
                transactionService.createTransaction(1, 2, -10.0, "Invalid")
        );

        assertThat(thrown)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Amount must be greater than zero");
    }

    @Test
    public void shouldThrowExceptionWhenAmountIsNull() {

        Throwable thrown = catchThrowable(() ->
                transactionService.createTransaction(1, 2, 0, "Invalid")
        );

        assertThat(thrown)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Amount must be greater than zero");
    }

    @Test
    public void shouldThrowExceptionWhenUsersAreNotConnected() {

        AppUser sender = new AppUser();
        sender.setIdUser(1);

        AppUser receiver = new AppUser();
        receiver.setIdUser(2);

        when(userRepository.findById(1)).thenReturn(Optional.of(sender));
        when(userRepository.findById(2)).thenReturn(Optional.of(receiver));
        when(connectionRepository.existsByUserAndFriend(sender, receiver))
                .thenReturn(false);

        Throwable thrown = catchThrowable(() ->
                transactionService.createTransaction(1, 2, 50.0, "Payment")
        );

        assertThat(thrown)
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Users are not friends");
    }

    @Test
    public void shouldReturnSentTransactionsOfUser() {

        AppUser user = new AppUser();
        user.setEmail("user@mail.com");

        AppUser friend = new AppUser();
        friend.setUsername("friend");

        Transaction transaction = new Transaction();
        transaction.setSender(user);
        transaction.setReceiver(friend);
        transaction.setDescription("Déjeuner");
        transaction.setAmount(15.0);

        when(userRepository.findByEmail("user@mail.com")).thenReturn(Optional.of(user));
        when(transactionRepository.findTransactionsBySender(user)).thenReturn(List.of(transaction));

        List<Transaction> transactions = transactionService.getUserSentTransactions("user@mail.com");

        assertThat(transactions).hasSize(1);
        assertThat(transactions.get(0).getReceiver().getUsername()).isEqualTo("friend");
        assertThat(transactions.get(0).getDescription()).isEqualTo("Déjeuner");
        assertThat(transactions.get(0).getAmount()).isEqualTo(15.0);
    }

    @Test
    public void shouldThrowExceptionWhenDescriptionIsTooLong() {
        int senderId = 1;
        int receiverId = 2;
        double amount = 10.0;

        String longDescription = "bla".repeat(100);

        Throwable thrown = catchThrowable(() ->
                transactionService.createTransaction(senderId, receiverId, amount, longDescription)
        );

        assertThat(thrown)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("La description ne doit pas dépasser 200 caractères");
    }

}
