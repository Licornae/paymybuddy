package com.openclassrooms.paymybuddy.serviceTest;

import com.openclassrooms.paymybuddy.dto.UserUpdateDTO;
import com.openclassrooms.paymybuddy.model.AppUser;
import com.openclassrooms.paymybuddy.repository.ConnectionRepository;
import com.openclassrooms.paymybuddy.repository.TransactionRepository;
import com.openclassrooms.paymybuddy.repository.UserRepository;
import com.openclassrooms.paymybuddy.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @Mock
    private ConnectionRepository connectionRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private UserService userService;

    //Registration

    @Test
    public void shouldSaveUserWhenValid() {

        when(userRepository.existsByUsername("valid")).thenReturn(false);
        when(userRepository.existsByEmail("valid@mail.com")).thenReturn(false);
        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");

        userService.registerUser("valid", "valid@mail.com", "password");

        ArgumentCaptor<AppUser> captor = ArgumentCaptor.forClass(AppUser.class);
        verify(userRepository).save(captor.capture());

        AppUser savedUser = captor.getValue();
        assertThat(savedUser.getUsername()).isEqualTo("valid");
        assertThat(savedUser.getEmail()).isEqualTo("valid@mail.com");
        assertThat(savedUser.getPassword()).isEqualTo("encodedPassword");
        assertThat(savedUser.getRole()).isEqualTo("USER");
    }

    @Test
    public void shouldEncodePasswordBeforeSavingUser() {

        when(userRepository.existsByUsername(any())).thenReturn(false);
        when(userRepository.existsByEmail(any())).thenReturn(false);
        when(passwordEncoder.encode("Password")).thenReturn("encodedPassword");

        userService.registerUser("User", "secure@mail.com", "Password");

        ArgumentCaptor<AppUser> captor = ArgumentCaptor.forClass(AppUser.class);
        verify(userRepository).save(captor.capture());

        AppUser savedUser = captor.getValue();
        assertThat(savedUser.getPassword()).isEqualTo("encodedPassword");
        assertThat(savedUser.getPassword()).isNotEqualTo("Password");
    }

    @Test
    public void shouldThrowExceptionWhenUsernameAlreadyExists() {

        when(userRepository.existsByUsername("existingUser")).thenReturn(true);

        Throwable thrown = catchThrowable(() -> userService.registerUser("existingUser", "new@mail.com", "password"));

        assertThat(thrown).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Ce nom d'utilisateur existe déjà");

        verify(userRepository, never()).save(any());
    }

    @Test
    public void shouldThrowExceptionIfEmailAlreadyExists() {

        when(userRepository.existsByEmail("test@mail.com")).thenReturn(true);

        Throwable thrown = catchThrowable(() -> userService.registerUser("user", "test@mail.com", "password"));

        assertThat(thrown).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("L'email existe déjà");

        verify(userRepository, never()).save(any());
    }

    //Get user

    @Test
    public void shouldReturnUserWhenEmailExists() {

        AppUser user = new AppUser();
        user.setEmail("test@mail.com");

        when(userRepository.findByEmail("test@mail.com")).thenReturn(Optional.of(user));

        AppUser result = userService.getUserByEmail("test@mail.com");

        assertThat(result).isEqualTo(user);
    }

    @Test
    public void shouldThrowExceptionWhenEmailNotFound() {

        when(userRepository.findByEmail("unknown@mail.com")).thenReturn(Optional.empty());

        Throwable thrown = catchThrowable(() -> userService.getUserByEmail("unknown@mail.com"));

        assertThat(thrown).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Utilisateur introuvable");
    }

    //Update

    @Test
    public void shouldUpdateUsername() {

        AppUser user = new AppUser();
        user.setUsername("oldName");

        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(userRepository.existsByUsername("newName")).thenReturn(false);

        UserUpdateDTO dto = new UserUpdateDTO();
        dto.setUsername("newName");

        userService.updateUser(1, dto);

        assertThat(user.getUsername()).isEqualTo("newName");
        verify(userRepository).save(user);
    }

    @Test
    public void shouldThrowExceptionWhenUpdatingWithExistingUsername() {

        AppUser user = new AppUser();
        user.setUsername("old");

        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(userRepository.existsByUsername("existing")).thenReturn(true);

        UserUpdateDTO dto = new UserUpdateDTO();
        dto.setUsername("existing");

        Throwable thrown = catchThrowable(() -> userService.updateUser(1, dto));

        assertThat(thrown).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Ce nom d'utilisateur existe déjà");
    }

    @Test
    public void shouldEncodePasswordWhenUpdating() {

        AppUser user = new AppUser();

        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(passwordEncoder.encode("newPassword")).thenReturn("encodedPassword");

        UserUpdateDTO dto = new UserUpdateDTO();
        dto.setPassword("newPassword");

        userService.updateUser(1, dto);

        assertThat(user.getPassword()).isEqualTo("encodedPassword");
        verify(userRepository).save(user);
    }

    //Delete

    @Test
    public void shouldDeleteUserAndRelatedData() {

        AppUser user = new AppUser();

        when(userRepository.findById(1)).thenReturn(Optional.of(user));

        userService.deleteUser(1);

        verify(connectionRepository).deleteByUser(user);
        verify(connectionRepository).deleteByFriend(user);
        verify(transactionRepository).deleteBySender(user);
        verify(transactionRepository).deleteByReceiver(user);
        verify(userRepository).delete(user);
    }

    //Validate Username
    @Test
    public void shouldThrowExceptionWhenUsernameIsNull() {

        Throwable thrown = catchThrowable(() -> userService.registerUser(null, "mail@mail.com", "password"));

        assertThat(thrown).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Nom d'utilisateur obligatoire");

        verify(userRepository, never()).save(any());
    }

    @Test
    public void shouldThrowExceptionWhenUsernameIsBlank() {

        Throwable thrown = catchThrowable(() -> userService.registerUser("   ", "mail@mail.com", "password"));

        assertThat(thrown).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Nom d'utilisateur obligatoire");

        verify(userRepository, never()).save(any());
    }

    @Test
    public void shouldThrowExceptionWhenUsernameTooLong_register() {

        String longUsername = "bla".repeat(20);

        Throwable thrown = catchThrowable(() -> userService.registerUser(longUsername, "mail@mail.com", "password"));

        assertThat(thrown).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Le nom d'utilisateur ne doit pas dépasser 50 caractères");

        verify(userRepository, never()).save(any());
    }


    //Validate Email
    @Test
    public void shouldThrowExceptionWhenEmailIsNull() {

        Throwable thrown = catchThrowable(() -> userService.registerUser("user", null, "password"));

        assertThat(thrown).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Email obligatoire");

        verify(userRepository, never()).save(any());
    }

    @Test
    public void shouldThrowExceptionWhenEmailIsBlank() {

        Throwable thrown = catchThrowable(() -> userService.registerUser("user", "   ", "password"));

        assertThat(thrown).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Email obligatoire");

        verify(userRepository, never()).save(any());
    }

    @Test
    public void shouldThrowExceptionWhenEmailTooLong() {

        String longEmail = "bla".repeat(20);

        Throwable thrown = catchThrowable(() -> userService.registerUser("user", longEmail, "password"));

        assertThat(thrown).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Email trop long");

        verify(userRepository, never()).save(any());
    }

    //Validate password

    @Test
    public void shouldThrowExceptionWhenPasswordIsNull() {

        Throwable thrown = catchThrowable(() -> userService.registerUser("user", "mail@mail.com", null));

        assertThat(thrown).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Mot de passe obligatoire");

        verify(userRepository, never()).save(any());
    }

    @Test
    public void shouldThrowExceptionWhenPasswordIsBlank() {

        Throwable thrown = catchThrowable(() -> userService.registerUser("user", "mail@mail.com", "   "));

        assertThat(thrown).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Mot de passe obligatoire");

        verify(userRepository, never()).save(any());
    }

    @Test
    public void shouldThrowExceptionWhenPasswordTooLong() {

        String longPassword = "bla".repeat(90);

        Throwable thrown = catchThrowable(() -> userService.registerUser("user", "mail@mail.com", longPassword));

        assertThat(thrown).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Mot de passe trop long");

        verify(userRepository, never()).save(any());
    }
}
