package org.example.demo.Modal.DTO.Orders;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDTO {
    private Long id;
    private Long userId;
    private String status;
    private BigDecimal totalAmount;
    private BigDecimal printPrice;
    private BigDecimal shipPrice;
    private BigDecimal preShipPrice;
    private List<OrderItemDTO> orderItems;
}
