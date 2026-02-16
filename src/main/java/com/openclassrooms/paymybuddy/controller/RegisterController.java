package com.openclassrooms.paymybuddy.controller;

import com.openclassrooms.paymybuddy.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


/**
 * Controller responsible for user registration.
 * This controller handles:
 * - Displaying the registration form
 * - Processing user registration requests
 * Upon successful registration, the user is redirected to the login page.
 * Validation errors are handled and displayed using flash attributes.
 */
@Slf4j
@Controller
public class RegisterController {

    @Autowired
    UserService userService;

    /**
     * Displays the registration page.
     *
     * @return the name of the register view
     */
    @GetMapping("/register")
    public String showRegisterPage() {
        log.debug("Registration page accessed");

        return "register";
    }

    /**
     * Processes the registration form submission.
     * Creates a new user with the provided credentials.
     * If the registration is successful, the user is redirected
     * to the login page.
     *
     * @param username the chosen username
     * @param email    the user's email address
     * @param password the user's raw password
     * @param redirectAttributes used to pass success or error messages
     * @return redirect URL after registration attempt
     */
    @PostMapping("/register")
    public String registerUser(@RequestParam String username,
                               @RequestParam String email,
                               @RequestParam String password,
                               RedirectAttributes redirectAttributes) {

        log.info("Registration attempt for email={}", email);

        try {
            userService.registerUser(username, email, password);

            log.info("Registration successful for email={}", email);

            redirectAttributes.addFlashAttribute("success",
                    "Inscription réussie. Vous pouvez maintenant vous connecter.");

            return "redirect:/login";

        } catch (IllegalArgumentException e) {

            log.warn("Registration validation failed for email={}: {}",
                    email, e.getMessage());

            redirectAttributes.addFlashAttribute("error",
                    e.getMessage());

            return "redirect:/register";

        } catch (Exception e) {

            log.error("Unexpected error during registration for email={}",
                email, e);

            redirectAttributes.addFlashAttribute("error",
                "Une erreur est survenue lors de l'inscription.");

            return "redirect:/register";
    }
    }
}
