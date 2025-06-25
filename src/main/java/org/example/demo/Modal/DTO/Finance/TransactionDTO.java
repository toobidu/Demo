package org.example.demo.Modal.DTO.Finance;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.demo.Modal.DTO.Orders.OrderDTO;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionDTO {
    private Long id;
    private Long fromWalletId;
    private Long toWalletId;
    private BigDecimal amount;
    private String transactionType;
    private Long adminId;
    private String adminName;
    private Long orderId;
    private List<OrderDTO> orders;
    private String fromUserName;
    private String toUserName;
}
