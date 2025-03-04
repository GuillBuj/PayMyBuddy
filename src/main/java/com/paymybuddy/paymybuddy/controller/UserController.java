package com.paymybuddy.paymybuddy.controller;

import java.math.BigDecimal;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.paymybuddy.paymybuddy.dto.BalanceOperationDTO;
import com.paymybuddy.paymybuddy.dto.BuddiesDTO;
import com.paymybuddy.paymybuddy.dto.BuddyConnectionDTO;
import com.paymybuddy.paymybuddy.dto.BuddyForTransferDTO;
import com.paymybuddy.paymybuddy.dto.TransactionInListDTO;
import com.paymybuddy.paymybuddy.dto.TransactionRequestDTO;
import com.paymybuddy.paymybuddy.dto.UpdateUserDTO;
import com.paymybuddy.paymybuddy.model.User;
import com.paymybuddy.paymybuddy.service.TransactionService;
import com.paymybuddy.paymybuddy.service.UserService;

import jakarta.validation.Valid;
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

    // @GetMapping(produces = "application/json")
    // public ResponseEntity<UserDTO> getUserById(@RequestParam int id) {
    //     UserDTO userDTO = userService.getUserById(id);
    //     return ResponseEntity.ok(userDTO);
    // }

    // @PostMapping("/pay")
    // public ResponseEntity<Transaction> sendMoney(@RequestBody TransactionRequestDTO transactionDTO){
    //     Transaction transaction = transactionService.createTransaction(transactionDTO);
    //     return ResponseEntity.ok(transaction);
    // }

    @GetMapping("/deposit")
    public String showDepositPage(Model model) {
        log.debug("- GET /user/deposit");

        model.addAttribute("balanceOperationDTO", new BalanceOperationDTO("", BigDecimal.ZERO));
        
        return "deposit";
    }
    
    @PostMapping("/deposit")
    public String deposit(@ModelAttribute BalanceOperationDTO operation, @AuthenticationPrincipal UserDetails userDetails, RedirectAttributes redirectAttributes) {
        log.debug("- POST /user/deposit: {}", operation);
        
        String userEmail = userDetails.getUsername();
        
        try {
            userService.deposit(new BalanceOperationDTO(userEmail, operation.amount()));
            redirectAttributes.addFlashAttribute("successMessage", "Argent ajouté");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Erreur dépôt");
        }
        
        return "redirect:/user/transfer";
    }

    @GetMapping("/profile")
    public String getProfile(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        
        String email = userDetails.getUsername();
        log.debug("- GET /user/profile: {}", email);

        User user = userService.getUserByEmail(email);
        String username = user.getUsername();
        String userEmail = user.getEmail();

        model.addAttribute("username", username);
        model.addAttribute("email", userEmail);
        model.addAttribute("editMode", false);

        return "profile";
    }

    @GetMapping("/profile/edit")
    public String editProfile(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        
        String email = userDetails.getUsername();
        log.debug("- GET /user/profile/edit: {}", email);

        User user = userService.getUserByEmail(email);

        UpdateUserDTO updateUserDTO = new UpdateUserDTO();
        updateUserDTO.setId(user.getId());
        updateUserDTO.setUsername(user.getUsername());
        updateUserDTO.setEmail(user.getEmail());
        updateUserDTO.setPassword("");

        model.addAttribute("updateUserDTO", updateUserDTO);
        model.addAttribute("editMode", true);

        return "profile";
    }

    @PatchMapping("/profile")
    public String updateProfile(@Valid UpdateUserDTO updateUserDTO, 
                                BindingResult bindingResult,
                                @AuthenticationPrincipal UserDetails userDetails,
                                RedirectAttributes redirectAttributes
                                ) {
        log.debug("- PATCH /user/profile: {}", updateUserDTO);

        if (bindingResult.hasErrors()) {
            log.error("Validation errors: {}", bindingResult.getAllErrors());
            bindingResult.getFieldErrors().forEach(error -> 
                redirectAttributes.addFlashAttribute(error.getField() + "Error", error.getDefaultMessage()));
            return "redirect:/user/profile/edit";
        }

        //hors validation classique car pas d'update du mot de passe si vide
        String updatePassword = updateUserDTO.getPassword();
        if (updatePassword != null && !updatePassword.isEmpty()) {
            if (updatePassword.length() < 3) {
                log.error("- Password too short: {}", updatePassword);
                redirectAttributes.addFlashAttribute("errorMessage", "Le mot de passe doit contenir au moins 3 caractères");
                return "redirect:/user/profile/edit";
            }
        }
  
        User user = userService.getUserByEmail(userDetails.getUsername());

        userService.updateUser(new UpdateUserDTO(user.getId(), updateUserDTO.getUsername(), updateUserDTO.getEmail(), updateUserDTO.getPassword()));
        redirectAttributes.addFlashAttribute("successMessage", "Profil mis à jour");
        
        return "redirect:/user/profile";
    }

    @PostMapping("/transfer")
    public String handleTransfer(@RequestParam("buddy") String buddyEmail,
                                @RequestParam("amount") BigDecimal amount,
                                @RequestParam("description") String description,
                                Model model,
                                RedirectAttributes redirectAttributes,
                                @AuthenticationPrincipal UserDetails userDetails) {
        
        String email = userDetails.getUsername();
        TransactionRequestDTO transaction = new TransactionRequestDTO(email, buddyEmail, amount, description);
        log.debug("- POST /user/transfer: {}", transaction);

        transactionService.createTransaction(transaction);
        redirectAttributes.addFlashAttribute("successMessage", "Transfert réussi");

        return "redirect:/user/transfer";
    }

    @GetMapping("/transfer")
    public String showTransferForm( Model model,
                                    @AuthenticationPrincipal UserDetails userDetails,
                                    @RequestParam(defaultValue = "0") int page,
                                    @RequestParam(defaultValue = "8") int size
                                    ) {
        
        String email = userDetails.getUsername();
        log.debug("- GET /user/transfer: {}", email);

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
        
        String userEmail = userDetails.getUsername();
        BuddyConnectionDTO buddyConnectionDTO = new BuddyConnectionDTO(userEmail, buddyEmail);
        log.debug("- POST /user/relation: {}", buddyConnectionDTO);

        userService.addBuddy(buddyConnectionDTO);
        redirectAttributes.addFlashAttribute("successMessage", "Buddy ajouté avec succès");

        return "redirect:/user/relation";
    }

    @GetMapping("/relation")
    public String showRelationForm(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        
        String email = userDetails.getUsername();
        log.debug("- GET /user/relation: {}", email);

        User user = userService.getUserByEmail(email);
        BuddiesDTO buddies = userService.getBuddies(user.getId());

        model.addAttribute("buddies", buddies);

        return "relation";
    }

    @DeleteMapping("/relation/{id}")
    public String removeBuddy(@PathVariable int id, @AuthenticationPrincipal UserDetails userDetails) {
        BuddyConnectionDTO buddyConnection = new BuddyConnectionDTO(userDetails.getUsername(), userService.getUserById(id).email());
        log.debug("- DELETE /user/relation", buddyConnection);

        userService.removeBuddy(buddyConnection);
        
        return "redirect:/user/relation";
    }

}

