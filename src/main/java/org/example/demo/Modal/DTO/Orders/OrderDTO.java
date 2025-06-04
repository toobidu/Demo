package org.example.demo.Modal.DTO.Orders;

import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class OrderDTO {
    Long id;
    Long userId;
    String statusCode;
    BigDecimal totalPrice;
    BigDecimal printingPrice;
    BigDecimal shippingPrice;
    BigDecimal preShippingPrice;
    LocalDateTime createdDate;
    LocalDateTime updatedDate;
    List<OrderItemDTO> orderItems;
}
