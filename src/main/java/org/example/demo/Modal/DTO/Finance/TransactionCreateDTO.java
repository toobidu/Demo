package org.example.demo.Modal.DTO.Finance;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class TransactionCreateDTO {
    Long fromUserId;
    Long toUserId;
    Long userId;
    BigDecimal amount;
    String statusCode;
    String typeCode;
    Long orderId;
}
