package com.openclassrooms.paymybuddy.repositoryTest;

import com.openclassrooms.paymybuddy.model.Connection;
import com.openclassrooms.paymybuddy.model.ConnectionId;
import com.openclassrooms.paymybuddy.repository.ConnectionRepository;
import com.openclassrooms.paymybuddy.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import com.openclassrooms.paymybuddy.model.AppUser;
import static org.assertj.core.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
public class ConnectionRepositoryTest {

    @Autowired
    private ConnectionRepository connectionRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    public void shouldSaveConnectionBetweenTwoUsers() {

        AppUser user = new AppUser();
        user.setUsername("user");
        user.setEmail("user@mail.com");
        user.setPassword("password");
        user.setRole("USER");

        AppUser friend = new AppUser();
        friend.setUsername("friend");
        friend.setEmail("friend@mail.com");
        friend.setPassword("password");
        friend.setRole("USER");

        userRepository.save(user);
        userRepository.save(friend);

        Connection connection = new Connection();
        connection.setUser(user);
        connection.setFriend(friend);

        Connection saved = connectionRepository.save(connection);

        assertThat(saved.getUser().getUsername()).isEqualTo("user");
        assertThat(saved.getFriend().getUsername()).isEqualTo("friend");
    }

    @Test
    public void shouldFindConnectionByCompositeId() {

        AppUser user = new AppUser();
        user.setUsername("user");
        user.setEmail("user@mail.com");
        user.setPassword("password");
        user.setRole("USER");

        AppUser friend = new AppUser();
        friend.setUsername("friend");
        friend.setEmail("friend@mail.com");
        friend.setPassword("password");
        friend.setRole("USER");

        userRepository.save(user);
        userRepository.save(friend);

        Connection connection = new Connection();
        connection.setUser(user);
        connection.setFriend(friend);

        connectionRepository.save(connection);

        ConnectionId id = new ConnectionId(
                user.getIdUser(),
                friend.getIdUser()
        );

        assertThat(connectionRepository.findById(id)).isPresent();
    }

    @Test
    public void shouldReturnTrueIfConnectionExists() {

        AppUser user = new AppUser();
        user.setUsername("user");
        user.setEmail("user@mail.com");
        user.setPassword("pass");
        user.setRole("USER");
        user = userRepository.save(user);

        AppUser friend = new AppUser();
        friend.setUsername("friend");
        friend.setEmail("friend@mail.com");
        friend.setPassword("pass");
        friend.setRole("USER");
        friend = userRepository.save(friend);

        Connection connection = new Connection();
        connection.setUser(user);
        connection.setFriend(friend);
        connectionRepository.save(connection);

        boolean exists = connectionRepository.existsByUserAndFriend(user, friend);

        assertThat(exists).isTrue();
    }

    @Test
    public void shouldDeleteConnectionsByUser() {

        AppUser user = new AppUser();
        user.setUsername("user");
        user.setEmail("user@mail.com");
        user.setPassword("pass");
        user.setRole("USER");
        user = userRepository.save(user);

        AppUser friend = new AppUser();
        friend.setUsername("friend");
        friend.setEmail("friend@mail.com");
        friend.setPassword("pass");
        friend.setRole("USER");
        friend = userRepository.save(friend);

        Connection connection = new Connection();
        connection.setUser(user);
        connection.setFriend(friend);
        connectionRepository.save(connection);

        assertThat(connectionRepository.count()).isEqualTo(1);

        connectionRepository.deleteByUser(user);

        assertThat(connectionRepository.count()).isZero();
    }
}
