
package com.example.onlineshopping.controller;

import com.example.onlineshopping.model.Product;
import com.example.onlineshopping.service.ProductService;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Controller
public class CartController {

    private final ProductService productService;

    public CartController(ProductService productService) {
        this.productService = productService;
    }

    @SuppressWarnings("unchecked")
    private Map<Long, Integer> getCart(HttpSession session) {
        Map<Long, Integer> cart =
                (Map<Long, Integer>) session.getAttribute("cart");

        if (cart == null) {
            cart = new LinkedHashMap<>();
            session.setAttribute("cart", cart);
        }

        return cart;
    }

    @PostMapping("/cart/add/{id}")
    public String addToCart(@PathVariable Long id,
                            HttpSession session) {

        Product product = productService.getProductById(id);

        if (product == null || product.getQuantity() <= 0) {
            return "redirect:/";
        }

        Map<Long, Integer> cart = getCart(session);

        int currentQuantity = cart.getOrDefault(id, 0);

        if (currentQuantity < product.getQuantity()) {
            cart.put(id, currentQuantity + 1);
        }

        return "redirect:/cart";
    }

    @GetMapping("/cart")
    public String viewCart(HttpSession session, Model model) {

        Map<Long, Integer> cart = getCart(session);

        List<CartItem> cartItems = new ArrayList<>();
        double total = 0;

        for (Map.Entry<Long, Integer> entry : cart.entrySet()) {

            Product product =
                    productService.getProductById(entry.getKey());

            if (product != null) {
                CartItem item =
                        new CartItem(product, entry.getValue());

                cartItems.add(item);
                total += item.getSubtotal();
            }
        }

        model.addAttribute("cartItems", cartItems);
        model.addAttribute("total", total);

        return "cart";
    }

    @PostMapping("/cart/update/{id}")
    public String updateCart(@PathVariable Long id,
                             @RequestParam int quantity,
                             HttpSession session) {

        Map<Long, Integer> cart = getCart(session);

        Product product = productService.getProductById(id);

        if (product == null || quantity <= 0) {
            cart.remove(id);
        } else if (quantity <= product.getQuantity()) {
            cart.put(id, quantity);
        }

        return "redirect:/cart";
    }

    @GetMapping("/cart/remove/{id}")
    public String removeFromCart(@PathVariable Long id,
                                 HttpSession session) {

        Map<Long, Integer> cart = getCart(session);
        cart.remove(id);

        return "redirect:/cart";
    }

    public static class CartItem {

        private final Product product;
        private final int quantity;

        public CartItem(Product product, int quantity) {
            this.product = product;
            this.quantity = quantity;
        }

        public Product getProduct() {
            return product;
        }

        public int getQuantity() {
            return quantity;
        }

        public double getSubtotal() {
            return product.getPrice() * quantity;
        }
    }
}
