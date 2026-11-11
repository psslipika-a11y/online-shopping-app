
package com.example.onlineshopping.repository;

import com.example.onlineshopping.model.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

}
