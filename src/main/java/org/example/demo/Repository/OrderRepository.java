package org.example.demo.Repository;

import org.example.demo.Modal.Entity.Order;
import org.example.demo.Modal.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUser(User user);
    
    List<Order> findByOrderStatus(String status);
    
    @Query("SELECT o FROM Order o WHERE o.orderStatus IN :statuses")
    List<Order> findByOrderStatusIn(List<String> statuses);
    
    @Query("SELECT o FROM Order o WHERE o.orderStatus != 'cancelled'")
    List<Order> findAllNonCancelled();
}