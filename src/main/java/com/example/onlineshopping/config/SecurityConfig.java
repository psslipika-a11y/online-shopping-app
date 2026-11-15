package com.example.onlineshopping.config;

import com.example.onlineshopping.repository.UserAccountRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {
    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    UserDetailsService userDetailsService(UserAccountRepository users) {
        return username -> users.findByUsername(username)
            .map(account -> User.withUsername(account.getUsername())
                .password(account.getPassword())
                .roles(account.getRole())
                .build())
            .orElseThrow(() -> new UsernameNotFoundException("Account not found"));
    }

    @Bean
    DaoAuthenticationProvider authenticationProvider(
            UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }

    @Bean
    SecurityFilterChain securityFilterChain(
            HttpSecurity http, DaoAuthenticationProvider authenticationProvider) throws Exception {
        http.authenticationProvider(authenticationProvider)
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/", "/login", "/register", "/css/**", "/error").permitAll()
                .requestMatchers("/add-product", "/save-product", "/delete-product/**", "/admin/**").hasRole("ADMIN")
                .requestMatchers("/cart/**", "/checkout", "/place-order", "/order-confirmation/**",
                                 "/orders/**").hasAnyRole("USER", "ADMIN")
                .anyRequest().authenticated())
            .formLogin(form -> form
                .loginPage("/login")
                .defaultSuccessUrl("/", true)
                .permitAll())
            .logout(logout -> logout.logoutSuccessUrl("/").permitAll());
        return http.build();
    }
}
