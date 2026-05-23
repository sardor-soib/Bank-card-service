package com.example.bankcards.util;

import com.example.bankcards.dto.CardDTO;
import com.example.bankcards.entity.Card;
import org.mapstruct.Mapper;
import org.springframework.data.domain.Page;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CardMapper {

    CardDTO toCardDTO(Card card);

    Card toCard(CardDTO cardDTO);

    List<Card> toCardList(List<CardDTO> cardDTOList);

    List<CardDTO> toCardDTOList(List<Card> cardList);

    default Page<CardDTO> toCardDTOPage(Page<Card> cardPage) {
        return cardPage.map(this::toCardDTO);
    }
}
