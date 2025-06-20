package org.example.demo.Service.Interface;

import org.example.demo.Modal.DTO.Finance.WalletDTO;

import java.math.BigDecimal;

public interface IWalletService {
    WalletDTO deposit(Long userId, BigDecimal amount, Long adminId);

    void deductBalance(Long userId, BigDecimal amount);

    void creditAdmin(BigDecimal amount);

    void creditPrinthouse(Long printerHouseId, BigDecimal amount);

    void refundOnCancel(Long userId, BigDecimal amount);

    WalletDTO getWallet(Long userId);
}
