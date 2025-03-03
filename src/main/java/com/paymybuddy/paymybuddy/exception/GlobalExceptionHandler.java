package com.paymybuddy.paymybuddy.exception;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {
    
    // @ExceptionHandler(AlreadyExistsException.class)
    // public ResponseEntity<ErrorMessage> resourceAlreadyExistsException(AlreadyExistsException e, WebRequest request){
    //     ErrorMessage message = new ErrorMessage(
    //             HttpStatus.BAD_REQUEST.value(),
    //             LocalDateTime.now(),
    //             e.getMessage(),
    //             request.getDescription(false));
    //     log.error(message.toString(), e);
    //     return new ResponseEntity<ErrorMessage>(message, HttpStatus.BAD_REQUEST);
    // }

    // @ExceptionHandler(UsernameAlreadyTakenException.class)
    // public String handleUsernameAlreadyTakenException(
    //             UsernameAlreadyTakenException e,
    //             RedirectAttributes redirectAttributes,
    //             HttpServletRequest request){
        
    //     ErrorMessage message = new ErrorMessage(
    //         HttpStatus.BAD_REQUEST.value(),
    //         LocalDateTime.now(),
    //         e.getMessage(),
    //         request.getRequestURI());

    //     log.error("UsernameAlreadyTakenException: {}", message.toString(), e);

    //     redirectAttributes.addFlashAttribute("errorUsername", "**Nom d'utilisateur déjà pris...");

    //     String referer = request.getHeader("Referer");
    //     log.warn(referer, message, referer);
    //     return "redirect:" + referer + "";
    // }
  

    // @ExceptionHandler(NotFoundException.class)
    // public ResponseEntity<ErrorMessage> resourceNotFoundException(NotFoundException e, WebRequest request){
    //     ErrorMessage message = new ErrorMessage(
    //             HttpStatus.NOT_FOUND.value(),
    //             LocalDateTime.now(),
    //             e.getMessage(),
    //             request.getDescription(false));
    //     log.error(message.toString(), e);
    //     return new ResponseEntity<ErrorMessage>(message, HttpStatus.NOT_FOUND);
    // }

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
