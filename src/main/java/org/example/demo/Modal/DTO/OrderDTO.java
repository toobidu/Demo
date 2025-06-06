package org.example.demo.Modal.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class OrderDTO {
    Long id;
    String orderStatus;
    BigDecimal totalPrice;
    UserDTO user;
    WalletDTO wallet;
    List<OrderItemDTO> orderDetails;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}
