package com.openclassrooms.paymybuddy.controllerTest;

import com.openclassrooms.paymybuddy.controller.ProfilController;
import com.openclassrooms.paymybuddy.dto.UserUpdateDTO;
import com.openclassrooms.paymybuddy.model.AppUser;
import com.openclassrooms.paymybuddy.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.mockito.ArgumentMatchers.any;

@WebMvcTest(ProfilController.class)
@AutoConfigureMockMvc(addFilters = false)
public class ProfilControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Test
    @WithMockUser(username = "user@mail.com")
    public void shouldDisplayProfilPage() throws Exception {

        AppUser user = new AppUser();
        user.setIdUser(1);
        user.setEmail("user@mail.com");

        when(userService.getUserByEmail("user@mail.com")).thenReturn(user);

        mockMvc.perform(get("/profil"))
                .andExpect(status().isOk())
                .andExpect(view().name("profil"))
                .andExpect(model().attributeExists("user"))
                .andExpect(model().attributeExists("userUpdateDTO"));
    }

    @Test
    @WithMockUser(username = "user@mail.com")
    public void shouldUpdateProfilSuccessfully() throws Exception {

        AppUser user = new AppUser();
        user.setIdUser(1);
        user.setEmail("user@mail.com");

        when(userService.getUserByEmail("user@mail.com")).thenReturn(user);

        mockMvc.perform(post("/profil/update")
                        .with(csrf())
                        .param("email", "user@mail.com"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/profil"))
                .andExpect(flash().attributeExists("success"));

        verify(userService).updateUser(eq(1), any(UserUpdateDTO.class));
    }

    @Test
    @WithMockUser(username = "user@mail.com")
    public void shouldInvalidateSessionWhenEmailChanged() throws Exception {

        AppUser user = new AppUser();
        user.setIdUser(1);
        user.setEmail("user@mail.com");

        when(userService.getUserByEmail("user@mail.com")).thenReturn(user);

        mockMvc.perform(post("/profil/update")
                        .with(csrf())
                        .param("email", "new@mail.com"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"))
                .andExpect(flash().attributeExists("success"));
    }

    @Test
    @WithMockUser(username = "user@mail.com")
    public void shouldHandleValidationException() throws Exception {

        AppUser user = new AppUser();
        user.setIdUser(1);

        when(userService.getUserByEmail("user@mail.com")).thenReturn(user);
        doThrow(new IllegalArgumentException("Erreur validation"))
                .when(userService).updateUser(eq(1), any(UserUpdateDTO.class));

        mockMvc.perform(post("/profil/update")
                        .with(csrf())
                        .param("email", "user@mail.com"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/profil"))
                .andExpect(flash().attributeExists("error"));
    }

    @Test
    @WithMockUser(username = "user@mail.com")
    public void shouldHandleUnexpectedException() throws Exception {

        AppUser user = new AppUser();
        user.setIdUser(1);

        when(userService.getUserByEmail("user@mail.com")).thenReturn(user);
        doThrow(new RuntimeException())
                .when(userService).updateUser(eq(1), any(UserUpdateDTO.class));

        mockMvc.perform(post("/profil/update")
                        .with(csrf())
                        .param("email", "user@mail.com"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/profil"))
                .andExpect(flash().attributeExists("error"));
    }

    @Test
    @WithMockUser(username = "user@mail.com")
    public void shouldDeleteAccountSuccessfully() throws Exception {

        AppUser user = new AppUser();
        user.setIdUser(1);

        when(userService.getUserByEmail("user@mail.com")).thenReturn(user);

        mockMvc.perform(post("/profil/delete")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"))
                .andExpect(flash().attributeExists("success"));

        verify(userService).deleteUser(1);
    }

    @Test
    @WithMockUser(username = "user@mail.com")
    public void shouldHandleDeleteException() throws Exception {

        AppUser user = new AppUser();
        user.setIdUser(1);

        when(userService.getUserByEmail("user@mail.com")).thenReturn(user);
        doThrow(new RuntimeException())
                .when(userService).deleteUser(1);

        mockMvc.perform(post("/profil/delete")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/profil"))
                .andExpect(flash().attributeExists("error"));
    }
}
