package com.openclassrooms.paymybuddy.serviceTest;

import com.openclassrooms.paymybuddy.configuration.CustomUserDetailsService;
import com.openclassrooms.paymybuddy.model.AppUser;
import com.openclassrooms.paymybuddy.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CustomUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CustomUserDetailsService customUserDetailsService;

    @Test
    public void shouldLoadUserByEmail() {

        AppUser user = new AppUser();
        user.setEmail("test@mail.com");
        user.setPassword("Password");
        user.setRole("USER");

        when(userRepository.findByEmail("test@mail.com")).thenReturn(Optional.of(user));

        UserDetails details = customUserDetailsService.loadUserByUsername("test@mail.com");

        assertThat(details.getUsername()).isEqualTo("test@mail.com");
        assertThat(details.getPassword()).isEqualTo("Password");
        assertThat(details.getAuthorities()).extracting("authority").containsExactly("ROLE_USER");

        verify(userRepository).findByEmail("test@mail.com");
    }

    @Test
    public void shouldThrowExceptionWhenUserNotFound() {

        when(userRepository.findByEmail("unknown@mail.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> customUserDetailsService.loadUserByUsername("unknown@mail.com"))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessage("User not found");

        verify(userRepository).findByEmail("unknown@mail.com");
    }
}
