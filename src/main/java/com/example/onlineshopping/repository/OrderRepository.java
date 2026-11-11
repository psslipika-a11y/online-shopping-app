
package com.example.onlineshopping.repository;

import com.example.onlineshopping.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {

}
