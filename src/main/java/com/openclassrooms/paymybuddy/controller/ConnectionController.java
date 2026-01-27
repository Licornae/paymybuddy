package com.openclassrooms.paymybuddy.controller;


import com.openclassrooms.paymybuddy.service.ConnectionService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


@Controller
@RequestMapping("/connections")
public class ConnectionController {

    @Autowired
    private ConnectionService connectionService;

    @GetMapping("/add")
    public String showAddConnectionPage() {
        return "connections";
    }

    @PostMapping("/add")
    public String addConnection(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam("email") String email,
            Model model, RedirectAttributes redirectAttributes) {

        try {
            connectionService.addConnectionByEmail(userDetails.getUsername(), email);

            redirectAttributes.addFlashAttribute(
                    "success", "Relation ajoutée avec succès"
            );

        } catch (IllegalArgumentException e) {

            redirectAttributes.addFlashAttribute(
                    "error", e.getMessage()
            );

        } catch (Exception e) {

            redirectAttributes.addFlashAttribute(
                    "error", "Une erreur est survenue, veuillez réessayer"
            );
        }
        return "redirect:/connections/add";
    }
}
