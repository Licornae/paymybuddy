package com.openclassrooms.paymybuddy.controller;

import com.openclassrooms.paymybuddy.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Controller responsible for user registration.
 *
 * Handles the display of the registration form and the processing
 * of user registration requests.
 */
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
        return "register";
    }

    /**
     * Processes the registration form submission.
     * Creates a new user with the provided credentials and redirects
     * the user to the login page upon successful registration.
     *
     * @param username the chosen username
     * @param email    the user's email address
     * @param password the user's raw password
     * @return a redirection to the login page
     */
    @PostMapping("/register")
    public String registerUser(@RequestParam String username,
                               @RequestParam String email,
                               @RequestParam String password,
                               RedirectAttributes redirectAttributes) {

        try {
            userService.registerUser(username, email, password);

            redirectAttributes.addFlashAttribute("success", "Inscription réussie. Vous pouvez maintenant vous connecter.");

            return "redirect:/login";

        } catch (IllegalArgumentException e) {

            redirectAttributes.addFlashAttribute("error", e.getMessage());

            return "redirect:/register";
        }
    }
}
