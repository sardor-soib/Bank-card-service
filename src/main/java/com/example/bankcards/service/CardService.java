package com.example.bankcards.service;

import com.example.bankcards.dto.CardDTO;
import com.example.bankcards.entity.Card;
import com.example.bankcards.exception.ResourceNotFoundException;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.util.CardMapper;
import jakarta.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

@Service
@Validated
@Transactional
public class CardService implements CardManager {

    private static final Logger logger = LoggerFactory.getLogger(CardService.class);

    private final CardRepository cardRepository;
    private final CardMapper cardMapper;

    @Autowired
    public CardService(CardRepository cardRepository, CardMapper cardMapper) {
        this.cardRepository = cardRepository;
        this.cardMapper = cardMapper;
    }

    @Override
    public boolean isExists(@NotNull Long id) {
        logger.info("Checking if card exists with ID {}", id);
        return cardRepository.existsById(id);
    }

    @Override
    public CardDTO create(CardDTO cardDto) {
        logger.info("Creating card for cardDTO {}", cardDto);
        Card card = cardMapper.toCard(cardDto);
        return cardMapper.toCardDTO(cardRepository.save(card));
    }

    @Override
    public CardDTO findById(@NotNull Long id) {
        logger.info("Finding card by ID {}", id);
        Card card = cardRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Card with ID %d not found".formatted(id)));
        return cardMapper.toCardDTO(card);
    }

    @Override
    public Page<CardDTO> findByUserId(@NotNull Long userId, Pageable pageable) {
        logger.info("Finding cards by user ID {}", userId);
        Page<Card> cards = cardRepository.findByUserId(userId, pageable);
        return cardMapper.toCardDTOPage(cards);
    }

    @Override
    public Page<CardDTO> search(@NotNull String query, Pageable pageable) {
        logger.info("Searching cards for query {}", query);
        Page<Card> cards = cardRepository.findByLastFour(query, pageable);
        return cardMapper.toCardDTOPage(cards);
    }

    @Override
    public Page<CardDTO> findAll(Pageable pageable) {
        logger.info("Finding all cards");
        Page<Card> cards = cardRepository.findAll(pageable);
        return cardMapper.toCardDTOPage(cards);
    }

    @Override
    public CardDTO update(Long id, CardDTO cardDto) {
        logger.info("Updating card with ID {}", id);
        if (!isExists(id)) {
            logger.info("Card with ID {} is not exists", id);
            throw new ResourceNotFoundException("Card with ID %d not found".formatted(id));
        }
        Card card = cardMapper.toCard(cardDto);
        return cardMapper.toCardDTO(cardRepository.save(card));
    }

    @Override
    public void remove(Long id) {
        logger.info("Removing card with ID {}", id);
        if (!isExists(id)) {
            logger.info("Card with ID {} is not exists", id);
            throw new ResourceNotFoundException("Card with ID %d not found".formatted(id));
        }
        cardRepository.deleteById(id);
    }
}
