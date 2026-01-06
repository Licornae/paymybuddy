package com.openclassrooms.paymybuddy.repositoryTest;

import com.openclassrooms.paymybuddy.model.AppUser;
import com.openclassrooms.paymybuddy.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import static org.assertj.core.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    public void shouldSaveAndFindUserByEmail() {
        AppUser user = new AppUser();
        user.setUsername("testuser");
        user.setEmail("test@mail.com");
        user.setPassword("password");
        user.setRole("USER");

        userRepository.save(user);

        AppUser found = userRepository.findByEmail("test@mail.com");

        assertThat(found).isNotNull();
        assertThat(found.getEmail()).isEqualTo("test@mail.com");
    }

    @Test
    public void shouldReturnNullWhenEmailNotFound() {
        AppUser found = userRepository.findByEmail("unknown@mail.com");

        assertThat(found).isNull();
    }

    @Test
    public void shouldGenerateIdWhenSavingUser() {
        AppUser user = new AppUser();
        user.setUsername("iduser");
        user.setEmail("id@mail.com");
        user.setPassword("password");
        user.setRole("USER");

        AppUser saved = userRepository.save(user);

        assertThat(saved.getIdUser()).isGreaterThan(0);
    }

    @Test
    public void shouldNotAllowDuplicateEmail() {
        AppUser user1 = new AppUser();
        user1.setUsername("user1");
        user1.setEmail("duplicate@mail.com");
        user1.setPassword("password");
        user1.setRole("USER");

        AppUser user2 = new AppUser();
        user2.setUsername("user2");
        user2.setEmail("duplicate@mail.com");
        user2.setPassword("password");
        user2.setRole("USER");

        userRepository.save(user1);

        assertThatThrownBy(() -> userRepository.save(user2))
                .isInstanceOf(Exception.class);
    }

    @Test
    public void shouldNotAllowDuplicateUser() {
        AppUser user1 = new AppUser();
        user1.setUsername("user");
        user1.setEmail("user@mail.com");
        user1.setPassword("password");
        user1.setRole("USER");

        AppUser user2 = new AppUser();
        user2.setUsername("user");
        user2.setEmail("user1@mail.com");
        user2.setPassword("password");
        user2.setRole("USER");

        userRepository.save(user1);

        assertThatThrownBy(() -> userRepository.save(user2))
                .isInstanceOf(Exception.class);
    }

}
