package com.example.onlineshopping.controller;

import com.example.onlineshopping.model.Product;
import com.example.onlineshopping.service.ProductService;

import org.springframework.stereotype.Controller;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/")
public String home(Model model, Authentication authentication) {

    model.addAttribute("products", productService.getAllProducts());

    boolean loggedIn = authentication != null
            && authentication.isAuthenticated()
            && !authentication.getPrincipal().equals("anonymousUser");

    boolean isAdmin = loggedIn
            && authentication.getAuthorities().stream()
            .anyMatch(authority ->
                authority.getAuthority().equals("ROLE_ADMIN"));

    boolean isUser = loggedIn
            && authentication.getAuthorities().stream()
            .anyMatch(authority ->
                authority.getAuthority().equals("ROLE_USER"));

    model.addAttribute("loggedIn", loggedIn);
    model.addAttribute("isAdmin", isAdmin);
    model.addAttribute("isUser", isUser);

    return "index";
}

    @GetMapping("/add-product")
    public String addProductPage() {
        return "add-product";
    }

    @PostMapping("/save-product")
    public String saveProduct(@ModelAttribute Product product) {
        productService.saveProduct(product);
        return "redirect:/";
    }

    @PostMapping("/delete-product/{id}")
    public String deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return "redirect:/";
    }
}