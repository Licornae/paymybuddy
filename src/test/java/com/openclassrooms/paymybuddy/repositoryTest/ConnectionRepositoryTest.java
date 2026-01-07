package com.openclassrooms.paymybuddy.repositoryTest;

import com.openclassrooms.paymybuddy.model.Connection;
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

        assertThat(connectionRepository.count()).isEqualTo(1);
    }
}
