package com.example.bankcards.util.mapper;

import com.example.bankcards.dto.CardDTO;
import com.example.bankcards.dto.CreateCardDTO;
import com.example.bankcards.entity.Card;
import jakarta.validation.constraints.NotNull;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.data.domain.Page;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CardMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "binNumber", ignore = true)
    @Mapping(target = "lastFour", ignore = true)
    @Mapping(target = "maskedPan", ignore = true)
    @Mapping(target = "panHash", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "expirationDate", dateFormat = "yyyy-MM-dd")
    Card toCard(@NotNull CreateCardDTO createCardDTO);

    @Mapping(source = "user.fullName", target = "cardHolderName")
    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "expirationDate", target = "expirationDate", dateFormat = "yyyy-MM-dd")
    CardDTO toCardDTO(Card card);

    @Mapping(target = "user", ignore = true)
    @Mapping(target = "binNumber", ignore = true)
    @Mapping(target = "lastFour", ignore = true)
    @Mapping(target = "panHash", ignore = true)
    @Mapping(target = "cardBrand", ignore = true)
    @Mapping(target = "cardType", ignore = true)
    @Mapping(target = "expirationDate", dateFormat = "yyyy-MM-dd")
    Card toCard(CardDTO cardDTO);

    List<Card> toCardList(List<CardDTO> cardDTOList);

    List<CardDTO> toCardDTOList(List<Card> cardList);

    default Page<CardDTO> toCardDTOPage(Page<Card> cardPage) {
        return cardPage.map(this::toCardDTO);
    }
}
