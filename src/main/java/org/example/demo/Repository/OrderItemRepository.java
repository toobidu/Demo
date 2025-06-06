package org.example.demo.Repository;

import org.example.demo.Modal.Entity.Order;
import org.example.demo.Modal.Entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    List<OrderItem> findByOrder(Order order);
    
    void deleteByOrder(Order order);
}
