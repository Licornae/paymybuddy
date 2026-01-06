package com.openclassrooms.paymybuddy.repositoryTest;

import com.openclassrooms.paymybuddy.model.AppUser;
import com.openclassrooms.paymybuddy.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
;import static org.assertj.core.api.Assertions.*;

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

}
