package com.openclassrooms.paymybuddy.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controller responsible for handling money transfer views.
 *
 * This controller exposes the main transfer page accessible
 * to authenticated users.
 */
@Controller
public class TransferController {

    /**
     * Displays the transfer page.
     *
     * @return the name of the transfer view
     */
    @GetMapping("/transfert")
    public String showTransferPage(){
        return "transfert";
    }
}
