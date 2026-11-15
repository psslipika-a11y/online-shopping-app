package com.example.onlineshopping.controller;

import com.example.onlineshopping.model.Order;
import com.example.onlineshopping.repository.OrderRepository;
import com.example.onlineshopping.service.OrderService;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.Map;

@Controller
public class CheckoutController {
    private final OrderService orderService;
    private final OrderRepository orderRepository;

    public CheckoutController(OrderService orderService, OrderRepository orderRepository) {
        this.orderService = orderService;
        this.orderRepository = orderRepository;
    }

    @GetMapping("/checkout")
    public String checkout(HttpSession session) {
        Map<?, ?> cart = (Map<?, ?>) session.getAttribute("cart");
        return cart == null || cart.isEmpty() ? "redirect:/cart" : "checkout";
    }

    @PostMapping("/place-order")
    public String placeOrder(@RequestParam String customerName,
                             @RequestParam String phone,
                             @RequestParam String address,
                             HttpSession session,
                             Authentication authentication,
                             RedirectAttributes redirectAttributes) {
        @SuppressWarnings("unchecked")
        Map<Long, Integer> cart = (Map<Long, Integer>) session.getAttribute("cart");
        if (cart == null || cart.isEmpty()) return "redirect:/cart";

        if (customerName.isBlank() || address.isBlank() || !phone.matches("[0-9]{10}")) {
            redirectAttributes.addFlashAttribute("errorMessage", "Please enter valid customer details.");
            return "redirect:/checkout";
        }
        try {
            Order order = orderService.placeOrder(authentication.getName(),
                customerName, phone, address, cart);
            session.removeAttribute("cart");
            return "redirect:/order-confirmation/" + order.getId();
        } catch (IllegalStateException exception) {
            redirectAttributes.addFlashAttribute("errorMessage", exception.getMessage());
            return "redirect:/cart";
        }
    }

    @GetMapping("/order-confirmation/{id}")
    public String orderConfirmation(@PathVariable Long id, Authentication authentication, Model model) {
        Order order = orderRepository.findByIdAndUsername(id, authentication.getName()).orElse(null);
        if (order == null) return "redirect:/orders";
        model.addAttribute("order", order);
        return "order-confirmation";
    }

    @GetMapping("/orders")
    public String myOrders(Authentication authentication, Model model) {
        model.addAttribute("orders",
            orderRepository.findByUsernameOrderByOrderDateDesc(authentication.getName()));
        return "orders";
    }

    @PostMapping("/orders/{id}/cancel")
    public String cancelOrder(@PathVariable Long id, Authentication authentication,
                              RedirectAttributes redirectAttributes) {
        try {
            orderService.cancelOrder(id, authentication.getName());
            redirectAttributes.addFlashAttribute("message", "Your order has been cancelled.");
        } catch (IllegalStateException exception) {
            redirectAttributes.addFlashAttribute("errorMessage", exception.getMessage());
        }
        return "redirect:/orders";
    }
}
