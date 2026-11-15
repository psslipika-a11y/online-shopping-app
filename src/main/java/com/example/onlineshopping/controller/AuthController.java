package com.example.onlineshopping.controller;

import com.example.onlineshopping.model.UserAccount;
import com.example.onlineshopping.repository.UserAccountRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class AuthController {
    private final UserAccountRepository users;
    private final PasswordEncoder encoder;

    public AuthController(UserAccountRepository users, PasswordEncoder encoder) {
        this.users = users;
        this.encoder = encoder;
    }

    @GetMapping("/login")
    public String login() { return "login"; }

    @GetMapping("/register")
    public String register() { return "register"; }

    @PostMapping("/register")
    public String registerUser(@RequestParam String username,
                               @RequestParam String password,
                               Model model) {
        String cleanUsername = username == null ? "" : username.trim();
        if (cleanUsername.isBlank() || password == null || password.length() < 8) {
            model.addAttribute("error", "Enter a username and a password of at least 8 characters.");
            return "register";
        }
        if (users.existsByUsername(cleanUsername)) {
            model.addAttribute("error", "That username is already registered.");
            return "register";
        }
        // Public registration can create USER accounts only, never ADMIN accounts.
        users.save(new UserAccount(cleanUsername, encoder.encode(password), "USER"));
        return "redirect:/login?registered";
    }
}
