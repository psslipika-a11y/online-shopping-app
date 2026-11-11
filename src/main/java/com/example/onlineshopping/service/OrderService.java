
package com.example.onlineshopping.service;

import com.example.onlineshopping.model.Order;
import com.example.onlineshopping.model.OrderItem;
import com.example.onlineshopping.model.Product;
import com.example.onlineshopping.repository.OrderRepository;
import com.example.onlineshopping.repository.ProductRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    public OrderService(OrderRepository orderRepository,
                        ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
    }

    @Transactional
    public Order placeOrder(String customerName,
                            String phone,
                            String address,
                            Map<Long, Integer> cart) {

        if (cart == null || cart.isEmpty()) {
            throw new IllegalStateException("Shopping cart is empty.");
        }

        Order order = new Order();

        order.setCustomerName(customerName);
        order.setPhone(phone);
        order.setAddress(address);
        order.setOrderDate(LocalDateTime.now());

        double total = 0;

        // First, validate every product and its stock.
        for (Map.Entry<Long, Integer> entry : cart.entrySet()) {

            Product product = productRepository.findById(entry.getKey())
                    .orElseThrow(() ->
                            new IllegalStateException("Product not found."));

            int quantity = entry.getValue();

            if (quantity <= 0 || quantity > product.getQuantity()) {
                throw new IllegalStateException(
                        "Insufficient stock for " + product.getName());
            }

            total += product.getPrice() * quantity;
        }

        // Then create order items and update stock.
        for (Map.Entry<Long, Integer> entry : cart.entrySet()) {

            Product product = productRepository.findById(entry.getKey())
                    .orElseThrow(() ->
                            new IllegalStateException("Product not found."));

            int quantity = entry.getValue();

            OrderItem item = new OrderItem();

            item.setProductName(product.getName());
            item.setPrice(product.getPrice());
            item.setQuantity(quantity);

            order.addItem(item);

            product.setQuantity(product.getQuantity() - quantity);
            productRepository.save(product);
        }

        order.setTotalAmount(total);

        return orderRepository.save(order);
    }
}
