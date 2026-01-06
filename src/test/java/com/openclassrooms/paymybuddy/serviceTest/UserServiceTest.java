package com.openclassrooms.paymybuddy.serviceTest;

import com.openclassrooms.paymybuddy.model.AppUser;
import com.openclassrooms.paymybuddy.repository.UserRepository;
import com.openclassrooms.paymybuddy.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    public void shouldThrowExceptionWhenEmailAlreadyExists() {

        AppUser existingUser = new AppUser();
        existingUser.setEmail("test@mail.com");

        when(userRepository.findByEmail("test@mail.com"))
                .thenReturn(existingUser);

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

        when(userRepository.findByUsername("existingUser"))
                .thenReturn(existingUser);

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

}
