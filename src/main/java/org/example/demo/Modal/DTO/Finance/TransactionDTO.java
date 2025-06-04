package org.example.demo.Modal.DTO.Finance;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.example.demo.Modal.DTO.Orders.OrderDTO;
import org.example.demo.Modal.DTO.Users.UserDTO;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TransactionDTO {
    Long id;
    Long fromUserId;
    Long toUserId;
    Long userId;
    Double amount;
    String statusCode;
    String typeCode;
    Long orderId;
    LocalDateTime createdDate;
    LocalDateTime updatedDate;
    OrderDTO order;
    UserDTO user;
    UserDTO fromUser;
    UserDTO toUser;
}
