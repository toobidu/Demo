package org.example.demo.Modal.DTO.Finance;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WalletDTO {
    private String walletName;
    private BigDecimal balance;
    private List<TransactionDTO> transactions;
}
