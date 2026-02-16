package com.openclassrooms.paymybuddy.controller;


import com.openclassrooms.paymybuddy.service.ConnectionService;
import lombok.extern.slf4j.Slf4j;
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

/**
 * Controller responsible for managing user connections.*
 * Handles HTTP requests related to adding new connections between users.
 * Only authenticated users can create connections.
 *- Displays the connection creation page
 *- Processes connection creation requests
 *- Handles success and error feedback via flash attributes
 * Uses {@link ConnectionService} to apply business rules.
 */
@Slf4j
@Controller
@RequestMapping("/connections")
public class ConnectionController {

    @Autowired
    private ConnectionService connectionService;

    /**
     * Displays the page allowing a user to add a new connection.
     *
     * @return the name of the view for adding connections
     */
    @GetMapping("/add")
    public String showAddConnectionPage() {
        log.debug("Displaying add connection page");
        return "connections";
    }

    /**
     * Handles the submission of a new connection request.*
     * Retrieves the authenticated user from the security context
     * and attempts to create a connection with the provided email.
     *
     * @param userDetails the authenticated user (injected by Spring Security)
     * @param email the email address of the user to connect with
     * @param redirectAttributes used to pass success/error messages after redirect
     * @return redirect to the add connection page
     */
    @PostMapping("/add")
    public String addConnection(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam("email") String email,
            Model model, RedirectAttributes redirectAttributes) {

        log.info("User '{}' attempting to add connection with email '{}'", userDetails.getUsername(), email);

        try {
            connectionService.addConnectionByEmail(userDetails.getUsername(), email);

            redirectAttributes.addFlashAttribute("success",
                    "Relation ajoutée avec succès");

            log.info("Connection successfully created for user '{}'", userDetails.getUsername());

        } catch (IllegalArgumentException e) {

            log.warn("Business error while adding connection: {}", e.getMessage());

            redirectAttributes.addFlashAttribute("error",
                    e.getMessage());

        } catch (Exception e) {

            log.error("Unexpected error while adding connection", e);

            redirectAttributes.addFlashAttribute("error",
                    "Une erreur est survenue, veuillez réessayer");
        }
        return "redirect:/connections/add";
    }
}
