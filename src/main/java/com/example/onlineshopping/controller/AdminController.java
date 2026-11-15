package com.example.onlineshopping.controller;

import com.example.onlineshopping.repository.OrderRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminController {
    private final OrderRepository orders;
    public AdminController(OrderRepository orders) { this.orders = orders; }

    @GetMapping("/admin")
    public String dashboard(Model model) {
        model.addAttribute("orders", orders.findAll());
        return "admin";
    }
}
