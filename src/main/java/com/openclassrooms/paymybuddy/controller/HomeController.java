package com.openclassrooms.paymybuddy.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;


/**
 * Controller responsible for handling the application's root URL.
 * This controller manages the default entry point ("/") of the application.
 * When a user accesses the root URL, they are redirected to the login page.
 */
@Controller
public class HomeController {

    /**
     * Handles requests to the root URL ("/").
     * Redirects users to the login page.
     *
     * @return a redirect instruction to "/login"
     */
    @GetMapping("/")
        public String home() {
            return "redirect:/login";
        }
}
