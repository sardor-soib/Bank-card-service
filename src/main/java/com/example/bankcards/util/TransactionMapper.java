package com.example.bankcards.util;

import com.example.bankcards.dto.TransactionDTO;
import com.example.bankcards.entity.Transaction;
import jakarta.validation.constraints.NotNull;
import org.mapstruct.Mapper;
import org.springframework.data.domain.Page;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TransactionMapper {

    TransactionDTO toTransactionDTO(@NotNull Transaction transaction);

    Transaction toTransaction(@NotNull TransactionDTO transactionDTO);

    List<TransactionDTO> toTransactionDTOList(@NotNull List<Transaction> transactionList);

    List<Transaction> toTransactionList(@NotNull List<TransactionDTO> transactionDTOList);

    default Page<TransactionDTO> toTransactionDTOPage(@NotNull Page<Transaction> transactionPage) {
        return transactionPage.map(this::toTransactionDTO);
    }
}
