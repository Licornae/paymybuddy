package com.openclassrooms.paymybuddy.controller;

import com.openclassrooms.paymybuddy.dto.TransferFormDTO;
import com.openclassrooms.paymybuddy.service.ConnectionService;
import com.openclassrooms.paymybuddy.service.TransactionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Controller responsible for handling money transfer views.
 *
 * This controller exposes the main transfer page accessible
 * to authenticated users.
 */
@Controller
@RequestMapping("/transfert")
public class TransferController {

    @Autowired
    private TransactionService transactionService;

    @Autowired
    ConnectionService connectionService;

    @GetMapping
    public String showTransfertPage(
            @AuthenticationPrincipal UserDetails userDetails,
            Model model) {

        String email = userDetails.getUsername();

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

    @PostMapping
    public String submitTransfer(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @ModelAttribute("transferForm") TransferFormDTO form,
            BindingResult result,
            RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("error", "Formulaire invalide");
            return "redirect:/transfert";
        }

        try {
            String email = userDetails.getUsername();
            int senderId = transactionService.getUserByEmail(email).getIdUser();

            transactionService.createTransaction(
                    senderId,
                    form.getReceiverId(),
                    form.getAmount(),
                    form.getDescription()
            );

            redirectAttributes.addFlashAttribute("success", "Paiement effectué");

        } catch (IllegalArgumentException | IllegalStateException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/transfert";
    }

}
