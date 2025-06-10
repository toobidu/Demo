package org.example.demo.Repository;

import org.example.demo.Modal.Entity.Orders.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUserId(Long userId);

    @Query("SELECT o FROM Order o JOIN User u ON o.user.id = u.id WHERE o.status != 'cancelled' OR u.typeAccount != 'sale'")
    List<Order> findAllOrdersForAdmin();

    @Query("SELECT o FROM Order o WHERE o.status IN ('order', 'processing', 'shipping', 'done')")
    List<Order> findOrdersForPrintHouse();
}
