package com.openclassrooms.paymybuddy.controllerTest;

import com.openclassrooms.paymybuddy.controller.TransferController;
import com.openclassrooms.paymybuddy.dto.ConnectionDTO;
import com.openclassrooms.paymybuddy.model.AppUser;
import com.openclassrooms.paymybuddy.service.ConnectionService;
import com.openclassrooms.paymybuddy.service.TransactionService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import java.util.List;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@WebMvcTest(TransferController.class)
@AutoConfigureMockMvc(addFilters = false)
public class TransferControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TransactionService transactionService;

    @MockBean
    private ConnectionService connectionService;

    @Test
    @WithMockUser(username = "user@test.com")
    public void shouldDisplayTransferPage() throws Exception {

        when(connectionService.getUserConnectionsDto("user@test.com"))
                .thenReturn(List.of(new ConnectionDTO(1, "friend")));

        when(transactionService.getUserSentTransactionsDto("user@test.com"))
                .thenReturn(List.of());

        mockMvc.perform(get("/transfert"))
                .andExpect(status().isOk())
                .andExpect(view().name("transfert"))
                .andExpect(model().attributeExists("connections"))
                .andExpect(model().attributeExists("transactions"))
                .andExpect(model().attributeExists("transferForm"));
    }

    @Test
    @WithMockUser(username = "user@test.com")
    public void shouldProcessTransferSuccessfully() throws Exception {

        AppUser user = new AppUser();
        user.setIdUser(1);

        when(transactionService.getUserByEmail("user@test.com"))
                .thenReturn(user);

        mockMvc.perform(post("/transfert")
                        .with(csrf())
                        .param("receiverId", "2")
                        .param("amount", "10")
                        .param("description", "Lunch"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/transfert"))
                .andExpect(flash().attributeExists("success"));

        verify(transactionService).createTransaction(1, 2, 10.0, "Lunch");
    }

    @Test
    @WithMockUser(username = "user@test.com")
    public void shouldRedirectWhenFormInvalid() throws Exception {

        mockMvc.perform(post("/transfert")
                        .with(csrf())
                        .param("receiverId", "")   // invalide
                        .param("amount", "")
                        .param("description", ""))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/transfert"))
                .andExpect(flash().attributeExists("error"));

        verify(transactionService, never()).createTransaction(anyInt(), anyInt(), anyDouble(), anyString());
    }

    @Test
    @WithMockUser(username = "user@test.com")
    public void shouldHandleBusinessException() throws Exception {

        AppUser user = new AppUser();
        user.setIdUser(1);

        when(transactionService.getUserByEmail("user@test.com"))
                .thenReturn(user);

        doThrow(new IllegalArgumentException("Solde insuffisant"))
                .when(transactionService)
                .createTransaction(anyInt(), anyInt(), anyDouble(), anyString());

        mockMvc.perform(post("/transfert")
                        .with(csrf())
                        .param("receiverId", "2")
                        .param("amount", "1000")
                        .param("description", "Big transfer"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/transfert"))
                .andExpect(flash().attributeExists("error"));
    }

    @Test
    @WithMockUser(username = "user@test.com")
    public void shouldHandleUnexpectedException() throws Exception {

        AppUser user = new AppUser();
        user.setIdUser(1);

        when(transactionService.getUserByEmail("user@test.com"))
                .thenReturn(user);

        doThrow(new RuntimeException("DB crash"))
                .when(transactionService)
                .createTransaction(anyInt(), anyInt(), anyDouble(), anyString());

        mockMvc.perform(post("/transfert")
                        .with(csrf())
                        .param("receiverId", "2")
                        .param("amount", "10")
                        .param("description", "Test"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/transfert"))
                .andExpect(flash().attributeExists("error"));
    }
}
