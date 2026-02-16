package com.openclassrooms.paymybuddy.controllerTest;

import com.openclassrooms.paymybuddy.controller.ConnectionController;
import com.openclassrooms.paymybuddy.service.ConnectionService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import org.springframework.boot.autoconfigure.thymeleaf.ThymeleafAutoConfiguration;


@WebMvcTest(
        controllers = ConnectionController.class,
        excludeAutoConfiguration = ThymeleafAutoConfiguration.class
)
public class ConnectionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ConnectionService connectionService;

    @Test
    @WithMockUser
    public void shouldDisplayAddConnectionPage() throws Exception {
        mockMvc.perform(get("/connections/add"))
                .andExpect(status().isOk())
                .andExpect(view().name("connections"));
    }

    @Test
    @WithMockUser(username = "user@mail.com")
    public void shouldAddConnectionSuccessfully() throws Exception {

        mockMvc.perform(post("/connections/add")
                        .with(csrf())
                        .param("email", "friend@mail.com"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/connections/add"))
                .andExpect(flash().attributeExists("success"));

        verify(connectionService)
                .addConnectionByEmail("user@mail.com", "friend@mail.com");
    }

    @Test
    @WithMockUser(username = "user@mail.com")
    void shouldReturnBusinessErrorWhenConnectionFails() throws Exception {

        doThrow(new IllegalArgumentException("La connection existe déjà"))
                .when(connectionService)
                .addConnectionByEmail("user@mail.com", "friend@mail.com");

        mockMvc.perform(post("/connections/add")
                        .with(csrf())
                        .param("email", "friend@mail.com"))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists("error"))
                .andExpect(flash().attribute("error", "La connection existe déjà"));
    }

    @Test
    @WithMockUser(username = "user@mail.com")
    public void shouldReturnGenericErrorWhenUnexpectedExceptionOccurs() throws Exception {

        doThrow(new RuntimeException("DB crash"))
                .when(connectionService)
                .addConnectionByEmail("user@mail.com", "friend@mail.com");

        mockMvc.perform(post("/connections/add")
                        .with(csrf())
                        .param("email", "friend@mail.com"))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists("error"))
                .andExpect(flash().attribute("error", "Une erreur est survenue, veuillez réessayer"));
    }
}

