package com.paymybuddy.paymybuddy.controller;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.paymybuddy.paymybuddy.dto.BalanceOperationDTO;
import com.paymybuddy.paymybuddy.dto.BuddyConnectionDTO;
import com.paymybuddy.paymybuddy.dto.BuddyForTransferDTO;
import com.paymybuddy.paymybuddy.dto.TransactionListDTO;
import com.paymybuddy.paymybuddy.dto.TransactionRequestDTO;
import com.paymybuddy.paymybuddy.dto.UserDTO;
import com.paymybuddy.paymybuddy.exception.AlreadyExistsException;
import com.paymybuddy.paymybuddy.exception.NotFoundException;
import com.paymybuddy.paymybuddy.model.Transaction;
import com.paymybuddy.paymybuddy.model.User;
import com.paymybuddy.paymybuddy.service.TransactionService;
import com.paymybuddy.paymybuddy.service.UserService;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("/user")
@AllArgsConstructor
@Slf4j
public class UserController {

    @Autowired
    private final UserService userService;

    @Autowired
    private final TransactionService transactionService;

    @GetMapping(produces = "application/json")
    public ResponseEntity<UserDTO> getUserById(@RequestParam int id) {
        UserDTO userDTO = userService.getUserById(id);
        return ResponseEntity.ok(userDTO);
    }

    @PostMapping("/pay")
    public ResponseEntity<Transaction> sendMoney(@RequestBody TransactionRequestDTO transactionDTO){
        Transaction transaction = transactionService.createTransaction(transactionDTO);
        return ResponseEntity.ok(transaction);
    }

    //pour test transaction
    @PostMapping("/deposit")
    public ResponseEntity<String> deposit(@RequestBody BalanceOperationDTO operation){
        userService.deposit(operation);
        return ResponseEntity.ok("Money added");
    }

    @GetMapping("/transactions")
    public TransactionListDTO getTransactions(@RequestParam int id){
        return userService.getTransactions(id);
    }

    //web
    @GetMapping("/profile")
    public String getProfile(Model model, Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            String email = authentication.getName();
            User user = userService.getUserByEmail(email);

            String username = user.getUsername();
            String userEmail = user.getEmail();

            model.addAttribute("username", username);
            model.addAttribute("email", userEmail);

            return "profile";
        }

        return "redirect:/login";
    }

    @GetMapping("/transfer")
    public String getTransfer(/*@RequestParam("buddy") String buddyEmail,
                    @RequestParam("amount") BigDecimal amount,
                    @RequestParam("description") String description,*/
                    Model model, Authentication authentication){
        if (authentication != null && authentication.isAuthenticated()){
            String email = authentication.getName();
            User user = userService.getUserByEmail(email);

            BigDecimal balance = user.getBalance();
            TransactionListDTO transactions = userService.getTransactions(user);
            Set<BuddyForTransferDTO> buddies = user.getBuddies().stream()
                                .map(buddy -> new BuddyForTransferDTO(buddy.getId(), buddy.getUsername()))
                                .collect(Collectors.toSet());
            log.info(buddies.toString());

            model.addAttribute("balance", balance);
            model.addAttribute("transactions", transactions);
            model.addAttribute("buddies", buddies);

            return "transfer";
        }

        return "redirect:/profile";
    }

    // @GetMapping("/transfer")
    // public String showTransfer() {
    //     log.info("--- dans le get ---");
    //     return "transfer";
    // }

    @PostMapping("/relation")
    public String addBuddy(@RequestParam("buddyEmail") String buddyEmail, Authentication authentication, RedirectAttributes redirectAttributes){
        log.info("--- debut addBuddy ---");
        String userEmail = authentication.getName();
        BuddyConnectionDTO buddyConnectionDTO = new BuddyConnectionDTO(userEmail, buddyEmail);

        try{
            log.info("--- try avant addBuddy service ---");
            userService.addBuddy(buddyConnectionDTO);
            log.info("--- try apres addBuddy service ---");
            redirectAttributes.addFlashAttribute("successMessage", "Relation ajoutée avec succès!");
        } catch (NotFoundException e) {
            log.info("--- dans le catch pour NotFoundException ---");
            redirectAttributes.addFlashAttribute("errorMessage", "L'utilisateur avec cet email n'a pas été trouvé.");
            return "redirect:/user/relation";
        } catch (AlreadyExistsException e) {
            log.info("--- dans le catch pour AlreadyExistsException ---");
            redirectAttributes.addFlashAttribute("errorMessage", "Cette relation existe déjà entre les utilisateurs.");
            return "redirect:/user/relation";
        }

        log.info("--- apres try/catch ---");
        return "redirect:/user/relation";
    }

    @GetMapping("/relation")
    public String showRelationForm() {
        log.info("--- dans le get ---");
        return "relation";
    }
}

