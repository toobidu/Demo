package org.example.demo.Mapper;

import org.example.demo.Modal.DTO.Finance.TransactionDTO;
import org.example.demo.Modal.Entity.Finance.Transaction;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TransactionMapper {
    TransactionDTO toDTO(Transaction transaction);

    Transaction toEntity(TransactionDTO transactionDTO);
}
