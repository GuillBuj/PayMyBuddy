package com.paymybuddy.paymybuddy.exception;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {
        
    @ExceptionHandler(UsernameAlreadyTakenException.class)
    public String handleUsernameAlreadyTakenException(UsernameAlreadyTakenException e, RedirectAttributes redirectAttributes, HttpServletRequest request) {

        redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());

        String referer = request.getHeader("Referer");
        if (referer != null) {
            if (referer.contains("/register")) {
                return "redirect:/register";
            } else if (referer.contains("/profile/edit")) {
                return "redirect:/user/profile/edit";
            }
        }

        return "redirect:/";
    }

    @ExceptionHandler(EmailAlreadyUsedException.class)
    public String handleEmailAlreadyUsedException(EmailAlreadyUsedException e, RedirectAttributes redirectAttributes, HttpServletRequest request) {

        redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());

        String referer = request.getHeader("Referer");
        if (referer != null) {
            if (referer.contains("/register")) {
                return "redirect:/register";
            } else if (referer.contains("/profile/edit")) {
                return "redirect:/user/profile/edit";
            }
        }

        return "redirect:/";
    }
    
    @ExceptionHandler(NotEnoughMoneyException.class)
    public String handleNotEnoughMoneyException(NotEnoughMoneyException e, RedirectAttributes redirectAttributes){
        log.error("--- NotEnoughMoneyException ---", e.getMessage());

        redirectAttributes.addFlashAttribute("errorMessage", "Solde insuffisant pour cette opération");

        return "redirect:/user/transfer";
    }

    @ExceptionHandler(SelfSendException.class)
    public String handleSelfSendException(SelfSendException e, RedirectAttributes redirectAttributes){
        log.error("--- SelfSendException ---", e.getMessage());

        redirectAttributes.addFlashAttribute("errorMessage", "Vous ne pouvez pas vous payer vous-même");

        return "redirect:/user/transfer";
    }

    @ExceptionHandler(SelfAddException.class)
    public String handleSelfAddException(SelfAddException e, RedirectAttributes redirectAttributes){
        log.error("--- SelfAddException ---", e.getMessage());

        redirectAttributes.addFlashAttribute("errorMessage", "Vous ne pouvez pas vous ajouter vous-même");

        return "redirect:/user/relation";
    }
    
    @ExceptionHandler(BuddyAlreadyAddedException.class)
    public String handleBuddyAlreadyAddedException(BuddyAlreadyAddedException e, RedirectAttributes redirectAttributes){
        log.error("--- BuddyAlreadyAddedException ---", e.getMessage());

        redirectAttributes.addFlashAttribute("errorMessage", e.buddyEmail + " est déjà dans votre liste");

        return "redirect:/user/relation";
    }

    @ExceptionHandler(BuddyNotFoundException.class)
    public String handleBuddyNotFoundException(BuddyNotFoundException e, RedirectAttributes redirectAttributes){
        log.error("--- BuddyNotFoundException ---", e.getMessage());

        redirectAttributes.addFlashAttribute("errorMessage", "Aucun compte avec l'adresse " + e.buddyEmail);

        return "redirect:/user/relation";
    }
}
