package com.paymybuddy.paymybuddy.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping
public class BaseController {
    @GetMapping("/base")
        public String showBasePage() {
            return "base";
        }
    // @GetMapping("/profile")
    // public String showProfilePage() {
    //     return "profile";
    // }
}
