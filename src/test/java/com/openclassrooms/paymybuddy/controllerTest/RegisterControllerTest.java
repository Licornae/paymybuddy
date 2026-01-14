package com.openclassrooms.paymybuddy.controllerTest;

import com.openclassrooms.paymybuddy.controller.RegisterController;
import com.openclassrooms.paymybuddy.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RegisterController.class)
public class RegisterControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Test
    @WithMockUser
    public void shouldRegisterUserAndRedirectToLogin() throws Exception {
        mockMvc.perform(post("/register")
                        .with(csrf())
                        .param("username", "User")
                        .param("email", "user@test.com")
                        .param("password", "password"))
                .andExpect(redirectedUrl("/login"));

        verify(userService).registerUser("User", "user@test.com", "password");
    }
}
