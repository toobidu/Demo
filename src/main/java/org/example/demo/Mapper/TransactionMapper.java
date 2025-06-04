package org.example.demo.Mapper;

import org.example.demo.Modal.DTO.Finance.TransactionCreateDTO;
import org.example.demo.Modal.DTO.Finance.TransactionDTO;
import org.example.demo.Modal.Entity.Finance.Transaction;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TransactionMapper {
    TransactionDTO toDTO(Transaction transaction);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "fromUser", ignore = true)
    @Mapping(target = "toUser", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "order", ignore = true)
    Transaction toEntity(TransactionCreateDTO transactionCreateDTO);

    List<TransactionDTO> toDTOList(List<Transaction> transactions);
}
