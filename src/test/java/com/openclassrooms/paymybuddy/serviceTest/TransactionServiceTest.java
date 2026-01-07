package com.openclassrooms.paymybuddy.serviceTest;

import com.openclassrooms.paymybuddy.model.AppUser;
import com.openclassrooms.paymybuddy.model.Connection;
import com.openclassrooms.paymybuddy.model.Transaction;
import com.openclassrooms.paymybuddy.repository.ConnectionRepository;
import com.openclassrooms.paymybuddy.repository.TransactionRepository;
import com.openclassrooms.paymybuddy.repository.UserRepository;
import com.openclassrooms.paymybuddy.service.ConnectionService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.Mock;

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

        Transaction savedTransaction = new Transaction();
        savedTransaction.setIdTransaction(1);
        savedTransaction.setAmount(100.0);

        when(transactionRepository.save(any(Transaction.class)))
                .thenReturn(savedTransaction);

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
                .hasMessage("Users are not connected");
    }
}
