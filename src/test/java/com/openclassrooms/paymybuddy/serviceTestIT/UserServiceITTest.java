package com.openclassrooms.paymybuddy.serviceTestIT;

import com.openclassrooms.paymybuddy.dto.UserUpdateDTO;
import com.openclassrooms.paymybuddy.model.AppUser;
import com.openclassrooms.paymybuddy.model.Connection;
import com.openclassrooms.paymybuddy.model.Transaction;
import com.openclassrooms.paymybuddy.repository.ConnectionRepository;
import com.openclassrooms.paymybuddy.repository.TransactionRepository;
import com.openclassrooms.paymybuddy.repository.UserRepository;
import com.openclassrooms.paymybuddy.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import static org.assertj.core.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.Optional;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class UserServiceITTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ConnectionRepository connectionRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    private AppUser user;

    @BeforeEach
    void setup() {
        user = new AppUser();
        user.setUsername("user");
        user.setEmail("user@mail.com");
        user.setPassword(passwordEncoder.encode("password123"));
        user.setRole("USER");
        user = userRepository.save(user);
    }

    //REGISTER

    @Test
    public void shouldRegisterUserSuccessfully() {

        userService.registerUser("newuser", "new@mail.com", "password123");

        Optional<AppUser> saved = userRepository.findByEmail("new@mail.com");

        assertThat(saved).isPresent();
        assertThat(passwordEncoder.matches("password123",
                saved.get().getPassword())).isTrue();
    }

    @Test
    public void shouldThrowWhenUsernameAlreadyExists() {

        Throwable thrown = catchThrowable(() ->
                userService.registerUser("user", "other@mail.com", "password123")
        );

        assertThat(thrown).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Ce nom d'utilisateur existe déjà");
    }

    @Test
    public void shouldThrowWhenEmailAlreadyExists() {

        Throwable thrown = catchThrowable(() ->
                userService.registerUser("new", "user@mail.com", "password123")
        );

        assertThat(thrown).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("L'email existe déjà");
    }

    @Test
    public void shouldThrowWhenUpdatingWithShortPassword() {

        UserUpdateDTO dto = new UserUpdateDTO();
        dto.setPassword("123");

        Throwable thrown = catchThrowable(() -> userService.updateUser(user.getIdUser(), dto));

        assertThat(thrown).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Mot de passe trop court");
    }

    @Test
    public void shouldThrowWhenPasswordTooShort() {

        Throwable thrown = catchThrowable(() ->
                userService.registerUser("test", "test@mail.com", "123")
        );

        assertThat(thrown).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Mot de passe trop court");
    }

    //GET USER

    @Test
    public void shouldReturnUserByEmail() {
        AppUser result = userService.getUserByEmail("user@mail.com");
        assertThat(result).isNotNull();
    }

    @Test
    public void shouldThrowWhenUserNotFound() {
        Throwable thrown = catchThrowable(() ->
                userService.getUserByEmail("unknown@mail.com")
        );

        assertThat(thrown).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Utilisateur introuvable");
    }

    //UPDATE USERNAME

    @Test
    public void shouldUpdateUsername() {

        UserUpdateDTO dto = new UserUpdateDTO();
        dto.setUsername("updated");

        userService.updateUser(user.getIdUser(), dto);

        AppUser updated = userRepository
                .findById(user.getIdUser())
                .orElseThrow();

        assertThat(updated.getUsername()).isEqualTo("updated");
    }

    @Test
    public void shouldThrowWhenUpdatingWithExistingUsername() {

        AppUser other = new AppUser();
        other.setUsername("taken");
        other.setEmail("taken@mail.com");
        other.setPassword("pass");
        other.setRole("USER");
        userRepository.save(other);

        UserUpdateDTO dto = new UserUpdateDTO();
        dto.setUsername("taken");

        Throwable thrown = catchThrowable(() ->
                userService.updateUser(user.getIdUser(), dto));

        assertThat(thrown).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Ce nom d'utilisateur existe déjà");
    }

    // UPDATE EMAIL

    @Test
    public void shouldUpdateEmail() {

        UserUpdateDTO dto = new UserUpdateDTO();
        dto.setEmail("updated@mail.com");

        userService.updateUser(user.getIdUser(), dto);

        AppUser updated = userRepository.findById(user.getIdUser())
                .orElseThrow();

        assertThat(updated.getEmail()).isEqualTo("updated@mail.com");
    }

    @Test
    public void shouldThrowWhenUpdatingWithExistingEmail() {

        AppUser other = new AppUser();
        other.setUsername("other");
        other.setEmail("existing@mail.com");
        other.setPassword("pass");
        other.setRole("USER");
        userRepository.save(other);

        UserUpdateDTO dto = new UserUpdateDTO();
        dto.setEmail("existing@mail.com");

        Throwable thrown = catchThrowable(() -> userService.updateUser(user.getIdUser(), dto));

        assertThat(thrown).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("L'email existe déjà");
    }


    // UPDATE PASSWORD

    @Test
    public void shouldUpdatePasswordEncoded() {

        UserUpdateDTO dto = new UserUpdateDTO();
        dto.setPassword("newpassword123");

        userService.updateUser(user.getIdUser(), dto);

        AppUser updated = userRepository.findById(user.getIdUser())
                .orElseThrow();

        assertThat(passwordEncoder.matches("newpassword123",
                updated.getPassword())).isTrue();
    }

    //UPDATE USER
    @Test
    public void shouldThrowWhenUpdatingUnknownUser() {

        UserUpdateDTO dto = new UserUpdateDTO();
        dto.setUsername("test");

        Throwable thrown = catchThrowable(() -> userService.updateUser(999, dto));

        assertThat(thrown).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Utilisateur introuvable");
    }

    @Test
    public void shouldNotChangeAnythingWhenDtoEmpty() {

        UserUpdateDTO dto = new UserUpdateDTO();

        userService.updateUser(user.getIdUser(), dto);

        AppUser unchanged = userRepository.findById(user.getIdUser())
                .orElseThrow();

        assertThat(unchanged.getUsername()).isEqualTo("user");
    }


    // DELETE USER

    @Test
    public void shouldDeleteUserAndAssociatedData() {

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

        Transaction transaction = new Transaction();
        transaction.setSender(user);
        transaction.setReceiver(friend);
        transaction.setAmount(20.0);
        transaction.setDateHeure(LocalDateTime.now());
        transactionRepository.save(transaction);

        userService.deleteUser(user.getIdUser());

        assertThat(userRepository.findById(user.getIdUser())).isEmpty();
        assertThat(connectionRepository.findAll()).isEmpty();
        assertThat(transactionRepository.findAll()).isEmpty();
    }

    @Test
    public void shouldThrowWhenDeletingUnknownUser() {

        Throwable thrown = catchThrowable(() -> userService.deleteUser(999));

        assertThat(thrown).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Utilisateur introuvable");
    }

}
