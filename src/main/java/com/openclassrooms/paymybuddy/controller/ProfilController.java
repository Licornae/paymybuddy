package com.openclassrooms.paymybuddy.controller;

import com.openclassrooms.paymybuddy.dto.UserUpdateDTO;
import com.openclassrooms.paymybuddy.model.AppUser;
import com.openclassrooms.paymybuddy.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Controller responsible for managing user profile operations.
 * This controller allows authenticated users to:
 * - View their profile
 * - Update their account information
 * - Delete their account
 * Security-sensitive actions such as email modification and account deletion
 * invalidate the current session to ensure proper re-authentication.
 */
@Slf4j
@Controller
public class ProfilController {

    @Autowired
    private UserService userService;

    /**
     * Displays the profile page of the authenticated user.
     *
     * @param userDetails the currently authenticated user
     * @param model       the Spring MVC model
     * @return the profile view name
     */
    @GetMapping("/profil")
    public String showProfilPage(
            @AuthenticationPrincipal UserDetails userDetails,
            Model model) {

        String email = userDetails.getUsername();
        log.debug("Loading profile page for user={}", email);

        AppUser user = userService.getUserByEmail(email);

        model.addAttribute("user", user);
        model.addAttribute("userUpdateDTO", new UserUpdateDTO());

        return "profil";
    }

    /**
     * Updates the profile information of the authenticated user.
     * If the email address is modified, the current session is invalidated
     * and the user is required to log in again.
     *
     * @param userDetails        authenticated user
     * @param dto                updated user data
     * @param redirectAttributes flash attributes for success/error messages
     * @param request            HTTP request (used for session invalidation)
     * @return redirect URL after update
     */
    @PostMapping("/profil/update")
    public String updateProfil(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @ModelAttribute UserUpdateDTO dto,
            RedirectAttributes redirectAttributes,
            HttpServletRequest request) {

        String currentEmail = null;
        try {

            currentEmail = userDetails.getUsername();
            log.info("User {} requested profile update", currentEmail);

            AppUser user = userService.getUserByEmail(currentEmail);

            boolean emailChanged = dto.getEmail() != null
                    && !dto.getEmail().isBlank()
                    && !dto.getEmail().equals(currentEmail);

            userService.updateUser(user.getIdUser(), dto);

            if (emailChanged) {

                log.info("User {} changed email. Session invalidated.", currentEmail);

                SecurityContextHolder.clearContext();

                request.getSession().invalidate();

                redirectAttributes.addFlashAttribute("success",
                        "Email modifié. Veuillez vous reconnecter."
                );

                return "redirect:/login";
            }

            log.info("Profile successfully updated for user {}", currentEmail);

            redirectAttributes.addFlashAttribute("success",
                    "Modification réussie"
            );

        } catch (IllegalArgumentException e) {


            log.warn("Profile update validation failed for user {}: {}", currentEmail, e.getMessage());

            redirectAttributes.addFlashAttribute("error",
                    e.getMessage()
            );

        } catch (Exception e) {

            log.error("Unexpected error during profile update for user {}", currentEmail, e);

            redirectAttributes.addFlashAttribute("error",
                    "Une erreur est survenue"
            );
        }

        return "redirect:/profil";
    }

    /**
     * Deletes the authenticated user's account.
     * After deletion:
     * - The security context is cleared
     * - The HTTP session is invalidated
     * - The user is redirected to the login page
     *
     * @param userDetails        authenticated user
     * @param request            HTTP request (used to invalidate session)
     * @param redirectAttributes flash attributes for feedback
     * @return redirect URL after deletion
     */
    @PostMapping("/profil/delete")
    public String deleteAccount(
            @AuthenticationPrincipal UserDetails userDetails,
            HttpServletRequest request,
            RedirectAttributes redirectAttributes) {

        String email = userDetails.getUsername();
        log.warn("User {} requested account deletion", email);

        try {

            AppUser user = userService.getUserByEmail(userDetails.getUsername());
            userService.deleteUser(user.getIdUser());

            SecurityContextHolder.clearContext();
            request.getSession().invalidate();

            log.warn("Account successfully deleted for user {}", email);

            redirectAttributes.addFlashAttribute("success",
                    "Compte supprimé avec succès"
            );

            return "redirect:/login";

        } catch (Exception e) {

            log.error("Error while deleting account for user {}", email, e);
            redirectAttributes.addFlashAttribute("error",
                    "Erreur lors de la suppression"
            );

            return "redirect:/profil";
        }
    }
}
