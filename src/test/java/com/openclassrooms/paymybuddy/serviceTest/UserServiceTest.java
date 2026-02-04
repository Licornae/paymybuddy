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
import static org.assertj.core.api.Assertions.*;
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
}
