package com.example.onlineshopping.config;

import com.example.onlineshopping.model.UserAccount;
import com.example.onlineshopping.repository.UserAccountRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class AdminBootstrap {
    @Bean
    CommandLineRunner createInitialAdmin(
            UserAccountRepository users,
            PasswordEncoder encoder,
            @Value("${app.admin.username:admin}") String username,
            @Value("${app.admin.password:}") String password) {
        return args -> {
            // No hard-coded/default admin password is created.
            if (password != null && !password.isBlank() && !users.existsByUsername(username)) {
                users.save(new UserAccount(username, encoder.encode(password), "ADMIN"));
            }
        };
    }
}
