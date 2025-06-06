package org.example.demo.Modal.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class TransactionDTO {
    Long id;
    BigDecimal amount;
    String transactionType;
    WalletDTO fromWallet;
    WalletDTO toWallet;
    LocalDateTime createdAt;
}
