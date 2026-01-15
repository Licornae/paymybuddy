package com.openclassrooms.paymybuddy.controllerTest;

import com.openclassrooms.paymybuddy.service.ConnectionService;
import com.openclassrooms.paymybuddy.model.AppUser;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ConnectionController.class)
public class ConnectionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ConnectionService connectionService;

    @Test
    @WithMockUser(username = "user@mail.com")
    public void shouldDisplayAddConnectionPage() throws Exception {

        mockMvc.perform(get("/connections/add"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "user@mail.com")
    public void shouldAddConnectionByEmail() throws Exception {

        mockMvc.perform(post("/connections/add")
                        .param("email", "friend@mail.com"))
                .andExpect(status().isOk())
                .andExpect(view().name("add-connection"))
                .andExpect(model().attributeExists("success"));

        verify(connectionService).addConnectionByEmail(
                org.mockito.ArgumentMatchers.any(AppUser.class),
                org.mockito.ArgumentMatchers.eq("friend@mail.com"));
    }
}
