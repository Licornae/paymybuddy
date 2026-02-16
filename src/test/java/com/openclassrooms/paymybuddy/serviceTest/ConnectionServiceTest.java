package com.openclassrooms.paymybuddy.serviceTest;

import com.openclassrooms.paymybuddy.dto.ConnectionDTO;
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

    //addConnection
    @Test
    public void shouldSaveConnectionWhenValid() {

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

        Throwable thrown = catchThrowable(() -> connectionService.addConnection(user, user));

        assertThat(thrown).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Vous ne pouvez pas vous ajouter vous-même");

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
                .hasMessage("La connection existe déjà");

        verify(connectionRepository, never()).save(any());
    }

    //addConnectionByEmail
    @Test
    void shouldAddConnectionByEmailWhenValid() {

        AppUser user = new AppUser();
        user.setIdUser(1);
        user.setEmail("current@mail.com");

        AppUser friend = new AppUser();
        friend.setIdUser(2);
        friend.setEmail("friend@mail.com");

        when(userRepository.findByEmail("current@mail.com"))
                .thenReturn(Optional.of(user));

        when(userRepository.findByEmail("friend@mail.com"))
                .thenReturn(Optional.of(friend));

        when(connectionRepository.existsByUserAndFriend(user, friend))
                .thenReturn(false);

        connectionService.addConnectionByEmail("current@mail.com", "friend@mail.com");

        verify(connectionRepository).save(any(Connection.class));
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

        assertThat(thrown).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Cet utilisateur n'est pas sur l'application");

        verify(connectionRepository, never()).save(any());
    }

    @Test
    public void shouldThrowExceptionWhenCurrentUserNotFound() {

        when(userRepository.findByEmail("current@mail.com"))
                .thenReturn(Optional.empty());

        Throwable thrown = catchThrowable(() ->
                connectionService.addConnectionByEmail("current@mail.com", "friend@mail.com")
        );

        assertThat(thrown).isInstanceOf(IllegalStateException.class)
                .hasMessage("Utilisateur connecté introuvable");

        verify(connectionRepository, never()).save(any());
    }

    //getUserConnectionsDto
    @Test
    public void shouldFindAllFriendsOfUser() {

        AppUser user = new AppUser();
        user.setIdUser(1);
        user.setEmail("user@mail.com");

        AppUser friend = new AppUser();
        friend.setIdUser(2);
        friend.setUsername("friend");

        Connection connection = new Connection();
        connection.setUser(user);
        connection.setFriend(friend);

        when(userRepository.findByEmail("user@mail.com")).thenReturn(Optional.of(user));
        when(connectionRepository.findConnectionsByUser(user)).thenReturn(List.of(connection));

        List<ConnectionDTO> friends = connectionService.getUserConnectionsDto("user@mail.com");

        assertThat(friends).hasSize(1);

        ConnectionDTO dto = friends.getFirst();
        assertThat(dto.id()).isEqualTo(2);
        assertThat(dto.username()).isEqualTo("friend");
    }

    @Test
    public void shouldReturnEmptyListWhenUserHasNoConnections() {

        AppUser user = new AppUser();
        user.setIdUser(1);

        when(userRepository.findByEmail("user@mail.com"))
                .thenReturn(Optional.of(user));

        when(connectionRepository.findConnectionsByUser(user))
                .thenReturn(List.of());

        List<ConnectionDTO> result = connectionService.getUserConnectionsDto("user@mail.com");

        assertThat(result).isEmpty();
    }

    @Test
    public void shouldThrowExceptionWhenUserNotFoundWhileFetchingConnections() {

        when(userRepository.findByEmail("unknown@mail.com"))
                .thenReturn(Optional.empty());

        Throwable thrown = catchThrowable(() -> connectionService.getUserConnectionsDto("unknown@mail.com"));

        assertThat(thrown).isInstanceOf(IllegalStateException.class)
                .hasMessage("Utilisateur introuvable");

        verify(connectionRepository, never()).findConnectionsByUser(any());
    }
}
