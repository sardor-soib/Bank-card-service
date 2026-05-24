package com.example.bankcards.util;

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
    Card toCard(@NotNull CreateCardDTO createCardDTO);

    CardDTO toCardDTO(Card card);

    Card toCard(CardDTO cardDTO);

    List<Card> toCardList(List<CardDTO> cardDTOList);

    List<CardDTO> toCardDTOList(List<Card> cardList);

    default Page<CardDTO> toCardDTOPage(Page<Card> cardPage) {
        return cardPage.map(this::toCardDTO);
    }
}
