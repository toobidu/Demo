package org.example.demo.Repository;

import org.example.demo.Modal.Entity.Orders.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    Page<Order> findByUserId(Long userId, Pageable pageable);

    @Query("SELECT o FROM Order o JOIN User u ON o.userId = u.id WHERE o.status != 'cancelled' OR u.typeAccount != 'sale'")
    Page<Order> findAllOrdersForAdmin(Pageable pageable);

    @Query("SELECT o FROM Order o WHERE o.status IN ('order', 'processing', 'shipping', 'done')")
    Page<Order> findOrdersForPrintHouse(Pageable pageable);
}
