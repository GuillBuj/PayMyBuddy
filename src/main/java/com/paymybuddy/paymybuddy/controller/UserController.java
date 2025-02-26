package com.paymybuddy.paymybuddy.controller;

import java.math.BigDecimal;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.paymybuddy.paymybuddy.dto.BalanceOperationDTO;
import com.paymybuddy.paymybuddy.dto.BuddiesDTO;
import com.paymybuddy.paymybuddy.dto.BuddyConnectionDTO;
import com.paymybuddy.paymybuddy.dto.BuddyForTransferDTO;
import com.paymybuddy.paymybuddy.dto.TransactionInListDTO;
import com.paymybuddy.paymybuddy.dto.TransactionRequestDTO;
import com.paymybuddy.paymybuddy.dto.UserDTO;
import com.paymybuddy.paymybuddy.exception.AlreadyExistsException;
import com.paymybuddy.paymybuddy.exception.NotEnoughMoneyException;
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

    //web
    @GetMapping("/profile")
    public String getProfile(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        
        String email = userDetails.getUsername();
        User user = userService.getUserByEmail(email);

        String username = user.getUsername();
        String userEmail = user.getEmail();

        model.addAttribute("username", username);
        model.addAttribute("email", userEmail);
        model.addAttribute("editMode", false);

        return "profile";
    }

    @PostMapping("/transfer")
    public String handleTransfer(@RequestParam("buddy") String buddyEmail,
                                @RequestParam("amount") BigDecimal amount,
                                @RequestParam("description") String description,
                                Model model,
                                RedirectAttributes redirectAttributes,
                                @AuthenticationPrincipal UserDetails userDetails) {
        
        try{ {
            String email = userDetails.getUsername();

            transactionService.createTransaction(new TransactionRequestDTO(email, buddyEmail, amount, description));

            redirectAttributes.addFlashAttribute("successMessage", "Transfer successful!");
            return "redirect:/user/transfer";
        }} catch(NotEnoughMoneyException e) {
            log.info("--- NotEnoughMoney ---");
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());}
        
        return "redirect:/user/transfer";
    }

    @GetMapping("/transfer")
    public String showTransferForm( Model model,
                                    @AuthenticationPrincipal UserDetails userDetails,
                                    @RequestParam(defaultValue = "0") int page,
                                    @RequestParam(defaultValue = "8") int size
                                    ) {
        
        String email = userDetails.getUsername();
        User user = userService.getUserByEmail(email);

        BigDecimal balance = user.getBalance();
        
        Page<TransactionInListDTO> transactionsPage = userService.getTransactionsPaginated(user.getId(), page, size);
        if (page < 0 || page >= transactionsPage.getTotalPages()) {
            page = 0;
        }
        BuddiesDTO buddies = new BuddiesDTO(user.getBuddies().stream()
                                .map(buddy -> new BuddyForTransferDTO(buddy.getId(), buddy.getUsername(), buddy.getEmail()))
                                .collect(Collectors.toSet()));

        model.addAttribute("balance", balance);
        model.addAttribute("transactions", transactionsPage);
        model.addAttribute("buddies", buddies);
        model.addAttribute("currentPage", transactionsPage.getNumber());
        model.addAttribute("totalPages", transactionsPage.getTotalPages());
        
        return "transfer";
    }

    @PostMapping("/relation")
    public String addBuddy(@RequestParam("buddyEmail") String buddyEmail, @AuthenticationPrincipal UserDetails userDetails, RedirectAttributes redirectAttributes){
        log.info("--- debut addBuddy ---");
        String userEmail = userDetails.getUsername();
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
    public String showRelationForm(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        String email = userDetails.getUsername();
        User user = userService.getUserByEmail(email);

        BuddiesDTO buddies = userService.getBuddies(user.getId());

        model.addAttribute("buddies", buddies);

        return "relation";
    }

    @DeleteMapping("/relation/{id}")
    public String deleteBuddy(@PathVariable int id, @AuthenticationPrincipal UserDetails userDetails) {
        userService.removeBuddy(new BuddyConnectionDTO(userDetails.getUsername(), userService.getUserById(id).email()));
        return "redirect:/user/relation";
    }

}

