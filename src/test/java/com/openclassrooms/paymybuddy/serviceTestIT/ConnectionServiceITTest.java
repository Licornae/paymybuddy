package com.openclassrooms.paymybuddy.serviceTestIT;

import com.openclassrooms.paymybuddy.dto.ConnectionDTO;
import com.openclassrooms.paymybuddy.model.AppUser;
import com.openclassrooms.paymybuddy.repository.ConnectionRepository;
import com.openclassrooms.paymybuddy.repository.UserRepository;
import com.openclassrooms.paymybuddy.service.ConnectionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class ConnectionServiceITTest {

    @Autowired
    private ConnectionService connectionService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ConnectionRepository connectionRepository;

    private AppUser user;
    private AppUser friend;

    @BeforeEach
    public void setup() {
        user = new AppUser();
        user.setUsername("user");
        user.setEmail("user@mail.com");
        user.setPassword("pass");
        user.setRole("USER");
        user = userRepository.save(user);

        friend = new AppUser();
        friend.setUsername("friend");
        friend.setEmail("friend@mail.com");
        friend.setPassword("pass");
        friend.setRole("USER");
        friend = userRepository.save(friend);
    }

    @Test
    public void shouldAddConnectionSuccessfully() {

        connectionService.addConnectionByEmail("user@mail.com", "friend@mail.com");

        assertThat(connectionRepository.count()).isEqualTo(1);
    }

    @Test
    public void shouldThrowExceptionWhenUserAddsHimself() {

        Throwable thrown = catchThrowable(() -> connectionService.addConnection(user, user));

        assertThat(thrown).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Vous ne pouvez pas vous ajouter vous-même");

        assertThat(connectionRepository.count()).isZero();
    }

    @Test
    public void shouldThrowExceptionWhenConnectionAlreadyExists() {

        connectionService.addConnection(user, friend);

        Throwable thrown = catchThrowable(() -> connectionService.addConnection(user, friend));

        assertThat(thrown).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("La connection existe déjà");

        assertThat(connectionRepository.count()).isEqualTo(1);
    }

    @Test
    public void shouldThrowExceptionWhenFriendNotFound() {

        Throwable thrown = catchThrowable(() ->
                connectionService.addConnectionByEmail("user@mail.com", "unknown@mail.com"));

        assertThat(thrown).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Cet utilisateur n'est pas sur l'application");

        assertThat(connectionRepository.count()).isZero();
    }

    @Test
    public void shouldReturnUserConnections() {

        connectionService.addConnection(user, friend);

        List<ConnectionDTO> result = connectionService.getUserConnectionsDto("user@mail.com");

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().username()).isEqualTo("friend");
    }

    @Test
    public void shouldReturnEmptyListWhenNoConnections() {

        List<ConnectionDTO> result = connectionService.getUserConnectionsDto("user@mail.com");

        assertThat(result).isEmpty();
    }
}
