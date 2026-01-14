package com.openclassrooms.paymybuddy.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controller responsible for handling authentication-related views.
 *
 * This controller exposes the login page used by Spring Security
 * for user authentication.
 */
@Controller
public class LoginController {

    /**
     * Displays the login page.
     *
     * @return the name of the login view
     */
    @GetMapping("/login")
    public String login() {
        return "login";
    }
}
