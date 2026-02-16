package com.openclassrooms.paymybuddy.controller;

import com.openclassrooms.paymybuddy.dto.TransferFormDTO;
import com.openclassrooms.paymybuddy.service.ConnectionService;
import com.openclassrooms.paymybuddy.service.TransactionService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Controller responsible for handling money transfer operations.

 * This controller allows authenticated users to:
 * - View their transfer page
 * - See their connections in the select
 * - See their sent transactions
 * - Send money to a connection
 * All operations require an authenticated user.
 */
@Slf4j
@Controller
@RequestMapping("/transfert")
public class TransferController {

    @Autowired
    private TransactionService transactionService;

    @Autowired
    ConnectionService connectionService;


    /**
     * Displays the transfer page for the authenticated user.
     *
     * @param userDetails the authenticated user
     * @param model       Spring MVC model
     * @return the transfer view name
     */
    @GetMapping
    public String showTransfertPage(
            @AuthenticationPrincipal UserDetails userDetails,
            Model model) {

        String email = userDetails.getUsername();
        log.debug("Loading transfer page for user={}", email);

        model.addAttribute(
                "connections",
                connectionService.getUserConnectionsDto(email)
        );

        model.addAttribute(
                "transactions",
                transactionService.getUserSentTransactionsDto(email)
        );

        model.addAttribute("transferForm", new TransferFormDTO());

        return "transfert";
    }

    /**
     * Processes a money transfer request.
     * Validates the transfer form and creates a new transaction
     * between the authenticated user and the selected receiver.
     *
     * @param userDetails        authenticated user
     * @param form               transfer form data
     * @param result             validation result
     * @param redirectAttributes flash messages
     * @return redirect to transfer page
     */
    @PostMapping
    public String submitTransfer(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @ModelAttribute("transferForm") TransferFormDTO form,
            BindingResult result,
            RedirectAttributes redirectAttributes) {

        String email = userDetails.getUsername();

        if (result.hasErrors()) {

            log.warn("Invalid transfer form submitted by user={}", email);

            redirectAttributes.addFlashAttribute("error", "Formulaire invalide");
            return "redirect:/transfert";
        }

        try {

            int senderId = transactionService.getUserByEmail(email).getIdUser();

            log.info("Transfer attempt from user={} to receiverId={} amount={}", email, form.getReceiverId(), form.getAmount());

            transactionService.createTransaction(
                    senderId,
                    form.getReceiverId(),
                    form.getAmount(),
                    form.getDescription()
            );

            log.info("Transfer successful for user={}", email);

            redirectAttributes.addFlashAttribute("success",
                    "Paiement effectué");

        } catch (IllegalArgumentException | IllegalStateException e) {

            log.warn("Business error during transfer for user={}: {}", email, e.getMessage());

            redirectAttributes.addFlashAttribute("error",
                    e.getMessage());

        } catch (Exception e) {

            log.error("Unexpected error during transfer for user={}", email, e);

            redirectAttributes.addFlashAttribute("error",
                "Une erreur est survenue lors du paiement");
        }

        return "redirect:/transfert";
    }
}


