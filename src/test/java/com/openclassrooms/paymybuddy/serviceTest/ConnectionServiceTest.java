package com.openclassrooms.paymybuddy.serviceTest;

import com.openclassrooms.paymybuddy.model.AppUser;
import com.openclassrooms.paymybuddy.model.Connection;
import com.openclassrooms.paymybuddy.repository.ConnectionRepository;
import com.openclassrooms.paymybuddy.repository.UserRepository;
import com.openclassrooms.paymybuddy.service.ConnectionService;
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
public class ConnectionServiceTest {

    @Mock
    private ConnectionRepository connectionRepository;

    @InjectMocks
    private ConnectionService connectionService;

    @Mock
    UserRepository userRepository;

    @Test
    void shouldSaveConnectionWhenValid() {

        AppUser user = new AppUser();
        user.setIdUser(1);

        AppUser friend = new AppUser();
        friend.setIdUser(2);

        when(connectionRepository.existsByUserAndFriend(user, friend))
                .thenReturn(false);

        connectionService.addConnection(user, friend);

        verify(connectionRepository).save(any(Connection.class));
    }

    @Test
    public void shouldThrowExceptionWhenUserAddsHimself() {

        AppUser user = new AppUser();
        user.setIdUser(1);

        Throwable thrown = catchThrowable(() ->
                connectionService.addConnection(user, user));

        assertThat(thrown)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Cannot add yourself as a friend");

        verify(connectionRepository, never()).save(any());
    }

    @Test
    public void shouldThrowExceptionWhenConnectionAlreadyExists() {

        AppUser user = new AppUser();
        user.setIdUser(1);

        AppUser friend = new AppUser();
        friend.setIdUser(2);

        when(connectionRepository.existsByUserAndFriend(user, friend))
                .thenReturn(true);

        Throwable thrown = catchThrowable(() ->
                connectionService.addConnection(user, friend));

        assertThat(thrown)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Connection already exists");

        verify(connectionRepository, never()).save(any());
    }

    @Test
    public void shouldThrowExceptionWhenFriendEmailNotFound() {

        AppUser user = new AppUser();
        user.setIdUser(1);
        user.setEmail("user@mail.com");

        when(userRepository.findByEmail("user@mail.com"))
                .thenReturn(Optional.of(user));

        when(userRepository.findByEmail("friend@mail.com"))
                .thenReturn(Optional.empty());

        Throwable thrown = catchThrowable(() ->
                connectionService.addConnectionByEmail("user@mail.com", "friend@mail.com")
        );

        assertThat(thrown)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Cet utilisateur n'est pas sur l'application");

        verify(connectionRepository, never()).save(any());
    }

    @Test
    void shouldFindAllFriendsOfUser() {

        AppUser user = new AppUser();
        user.setEmail("user@mail.com");

        AppUser friend = new AppUser();
        friend.setEmail("friend@mail.com");

        Connection connection = new Connection();
        connection.setUser(user);
        connection.setFriend(friend);

        when(userRepository.findByEmail("user@mail.com")).thenReturn(Optional.of(user));
        when(connectionRepository.findConnectionsByUser(user)).thenReturn(List.of(connection));

        List<AppUser> friends = connectionService.getUserConnections("user@mail.com");

        assertThat(friends).hasSize(1);
        assertThat(friends.get(0).getEmail()).isEqualTo("friend@mail.com");
    }
}
