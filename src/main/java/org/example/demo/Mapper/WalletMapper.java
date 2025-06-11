package org.example.demo.Mapper;

import org.example.demo.Modal.DTO.Finance.WalletDTO;
import org.example.demo.Modal.Entity.Finance.Wallet;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface WalletMapper {
    WalletDTO toDTO(Wallet wallet);

    Wallet toEntity(WalletDTO walletDTO);
}
