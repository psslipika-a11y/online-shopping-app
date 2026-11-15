package com.example.onlineshopping.repository;

import com.example.onlineshopping.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUsernameOrderByOrderDateDesc(String username);
    Optional<Order> findByIdAndUsername(Long id, String username);
}
