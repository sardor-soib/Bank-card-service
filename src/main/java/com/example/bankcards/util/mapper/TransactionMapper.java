package com.example.bankcards.util.mapper;

import com.example.bankcards.dto.TransactionDTO;
import com.example.bankcards.entity.Transaction;
import jakarta.validation.constraints.NotNull;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.data.domain.Page;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Mapper(componentModel = "spring")
public interface TransactionMapper {

    @Mapping(source = "transactionType", target = "type")
    @Mapping(source = "transactionTime", target = "date")
    @Mapping(source = "card.id", target = "cardId")
    @Mapping(source = "user.id", target = "userId")
    TransactionDTO toTransactionDTO(@NotNull Transaction transaction);

    Transaction toTransaction(@NotNull TransactionDTO transactionDTO);

    List<TransactionDTO> toTransactionDTOList(@NotNull List<Transaction> transactionList);

    List<Transaction> toTransactionList(@NotNull List<TransactionDTO> transactionDTOList);

    default Page<TransactionDTO> toTransactionDTOPage(@NotNull Page<Transaction> transactionPage) {
        return transactionPage.map(this::toTransactionDTO);
    }

    default String map(OffsetDateTime dateTime) {
        if (dateTime == null) return null;
        return dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX"));
    }
}
