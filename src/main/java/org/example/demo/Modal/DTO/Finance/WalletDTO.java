package org.example.demo.Modal.DTO.Finance;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class WalletDTO {
    Long id;
    Long userId;
    BigDecimal balance;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}
