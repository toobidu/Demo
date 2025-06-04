package org.example.demo.Modal.DTO.Orders;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class OrderCreateDTO {
    Long userId;
    String statusCode;
    BigDecimal totalPrice;
    BigDecimal printingPrice;
    BigDecimal shippingPrice;
    BigDecimal preShippingPrice;
    List<OrderItemCreateDTO> orderItems;
}
