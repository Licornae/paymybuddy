package com.openclassrooms.paymybuddy.serviceTest;

import com.openclassrooms.paymybuddy.model.AppUser;
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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;


    @InjectMocks
    private UserService userService;

    @Test
    public void shouldSaveUserWhenValid() {

        AppUser user = new AppUser();
        user.setUsername("valid");
        user.setEmail("valid@mail.com");
        user.setPassword("password");
        user.setRole("USER");

        when(userRepository.existsByEmail(any())).thenReturn(false);
        when(userRepository.existsByUsername(any())).thenReturn(false);
        when(userRepository.save(any())).thenReturn(user);

        AppUser saved = userService.saveUser(user);

        assertThat(saved).isNotNull();
        verify(userRepository).save(user);
    }

    @Test
    public void shouldThrowExceptionWhenEmailAlreadyExists() {

        AppUser existingUser = new AppUser();
        existingUser.setEmail("test@mail.com");

        when(userRepository.existsByEmail("test@mail.com"))
                .thenReturn(true);

        AppUser newUser = new AppUser();
        newUser.setUsername("newuser");
        newUser.setEmail("test@mail.com");
        newUser.setPassword("password");
        newUser.setRole("USER");

        Throwable thrown = catchThrowable(() -> userService.saveUser(newUser));

        assertThat(thrown)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Email already exists");

        verify(userRepository, never()).save(any()); // vérifie que userRepository.save() n’est jamais exécuté avec n’importe quel argument
    }

    @Test
    public void shouldThrowExceptionWhenUsernameAlreadyExists() {

        AppUser existingUser = new AppUser();
        existingUser.setUsername("existingUser");

        when(userRepository.existsByUsername("existingUser"))
                .thenReturn(true);

        AppUser newUser = new AppUser();
        newUser.setUsername("existingUser");
        newUser.setEmail("new@mail.com");
        newUser.setPassword("password");
        newUser.setRole("USER");

        Throwable thrown = catchThrowable(() -> userService.saveUser(newUser));

        assertThat(thrown)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Username already exists");

        verify(userRepository, never()).save(any());
    }

    @Test
    public void shouldEncodePasswordBeforeSavingUser() {

        AppUser user = new AppUser();
        user.setUsername("User");
        user.setEmail("secure@mail.com");
        user.setPassword("Password");
        user.setRole("USER");

        when(userRepository.existsByEmail(any())).thenReturn(false);
        when(userRepository.existsByUsername(any())).thenReturn(false);

        when(passwordEncoder.encode(any())).thenReturn("encodedPassword");
        when(passwordEncoder.matches("Password", "encodedPassword")).thenReturn(true);

        when(userRepository.save(any())).thenReturn(user);

        AppUser saved = userService.saveUser(user);

        assertThat(saved.getPassword()).isNotEqualTo("Password");

        assertThat(passwordEncoder.matches("Password", saved.getPassword())).isTrue();
    }

    @Test
    public void shouldRegisterUserWithEncodedPassword() {

        String username = "User";
        String email = "user@test.com";
        String rawPassword = "password";

        when(userRepository.findByEmail(email))
                .thenReturn(Optional.empty());
        when(userRepository.findByUsername(username))
                .thenReturn(null);

        when(passwordEncoder.encode(rawPassword))
                .thenReturn("encodedPassword");

        userService.registerUser(username, email, rawPassword);

        ArgumentCaptor<AppUser> captor = ArgumentCaptor.forClass(AppUser.class);
        verify(userRepository).save(captor.capture());

        AppUser savedUser = captor.getValue();
        assertEquals(username, savedUser.getUsername());
        assertEquals(email, savedUser.getEmail());
        assertEquals("encodedPassword", savedUser.getPassword());
        assertEquals("USER", savedUser.getRole());
    }

    @Test
    public void shouldThrowExceptionIfEmailAlreadyExists() {

        when(userRepository.findByEmail("user@test.com")).thenReturn(Optional.of(new AppUser()));

        assertThrows(IllegalArgumentException.class, () -> userService.registerUser("User","user@test.com", "password"));

        verify(userRepository, never()).save(any());
    }
}
