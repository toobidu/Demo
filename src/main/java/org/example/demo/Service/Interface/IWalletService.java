package org.example.demo.Service.Interface;

import org.example.demo.Modal.DTO.Finance.WalletDTO;

import java.math.BigDecimal;

public interface IWalletService {
    WalletDTO deposit(Long userId, BigDecimal amount, Long adminId);

    WalletDTO getWallet(Long userId);
}
