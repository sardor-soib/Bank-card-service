package com.example.bankcards.service.impl;

import com.example.bankcards.dto.CardDTO;
import com.example.bankcards.dto.CreateCardDTO;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.User;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.security.PanHashEncoder;
import com.example.bankcards.service.CardService;
import com.example.bankcards.service.TransactionService;
import com.example.bankcards.service.validation.TransactionValidator;
import com.example.bankcards.service.validation.UserValidator;
import com.example.bankcards.util.CardStatus;
import com.example.bankcards.util.PanMasker;
import com.example.bankcards.util.mapper.CardMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.math.BigDecimal;
import java.time.YearMonth;

@Service
@Validated
@Transactional
public class CardServiceImpl implements CardService {

    private static final Logger logger = LoggerFactory.getLogger(CardServiceImpl.class);

    private final CardRepository cardRepository;
    private final CardMapper cardMapper;
    private final PanHashEncoder panHashEncoder;
    private final TransactionService transactionService;
    private final UserRepository userRepository;

    @Autowired
    public CardServiceImpl(CardRepository cardRepository, CardMapper cardMapper, PanHashEncoder panHashEncoder, TransactionService transactionService, UserRepository userRepository) {
        this.cardRepository = cardRepository;
        this.cardMapper = cardMapper;
        this.panHashEncoder = panHashEncoder;
        this.transactionService = transactionService;
        this.userRepository = userRepository;
    }

    @Override
    public boolean isExists(Long id) {
        logger.info("Checking if card exists with ID {}", id);
        return cardRepository.existsById(id);
    }

    @Override
    public void transferBalance(Long userId, Long sourceCardId, Long targetCardId, BigDecimal amount) {

        logger.info("Transfer between user cards for userId {}, sourceCardId {}, targetCardId {}, amount {}", userId, sourceCardId, targetCardId, amount);

        Card sourceCard = getCardEntityById(sourceCardId);
        Card targetCard = getCardEntityById(targetCardId);

        TransactionValidator.validateTransferBetweenUserCards(userId, sourceCard, targetCard, amount);

        logger.info("Performing transfer between cards");
        sourceCard.debit(amount);
        targetCard.credit(amount);
        cardRepository.save(sourceCard);
        cardRepository.save(targetCard);
        logger.info("Transfer completed successfully");
        transactionService.formTransactions(sourceCard, targetCard, amount);
    }

    @Override
    public BigDecimal getBalance(Long userId, Long cardId) {
        logger.info("Getting balance for user with id {} and card with id {}", userId, cardId);
        Card card = getCardEntityById(cardId);
        UserValidator.validateOwner(userId, card);
        return card.getBalance();
    }

    @Override
    public void activateCard(Long id) {
        logger.info("Activating card with id {}", id);
        Card card = getCardEntityById(id);
        card.unblockCard();
        cardRepository.save(card);
    }

    @Override
    public void deactivateCard(Long id) {
        logger.info("Deactivating card with id {}", id);
        Card card = getCardEntityById(id);
        card.blockCard();
        cardRepository.save(card);
    }

    @Override
    public void requestCardBlock(Long userId, Long cardId) {
        logger.info("Requesting card block for card with id {} by user with id {}", cardId, userId);
        Card card = getCardEntityById(cardId);
        UserValidator.validateOwner(userId, card);
        card.requestBlock();
        cardRepository.save(card);
    }

    @Override
    public CardDTO create(CreateCardDTO createCardDTO) {
        logger.info("Creating card for cardDTO {}", createCardDTO);

        String rawPan = createCardDTO.pan();

        Card card = cardMapper.toCard(createCardDTO);

        User user = userRepository.findById(createCardDTO.userId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + createCardDTO.userId()));
        card.setUser(user);

        card.applyPanData(
                PanMasker.bin(rawPan),
                PanMasker.lastFour(rawPan),
                PanMasker.mask(rawPan),
                panHashEncoder.encode(rawPan)
        );

        return cardMapper.toCardDTO(cardRepository.save(card));
    }

    @Override
    public CardDTO findById(Long id) {
        logger.info("Finding card by ID {}", id);
        return cardMapper.toCardDTO(getCardEntityById(id));
    }

    @Override
    public Page<CardDTO> findByUserId(Long userId, Pageable pageable) {
        logger.info("Finding cards by user ID {}", userId);
        Page<Card> cards = cardRepository.findByUserId(userId, pageable);
        return cardMapper.toCardDTOPage(cards);
    }

    @Override
    public Page<CardDTO> search(String query, Pageable pageable) {
        logger.info("Searching cards for query {}", query);
        Page<Card> cards = cardRepository.findByLastFour(query, pageable);
        return cardMapper.toCardDTOPage(cards);
    }

    @Override
    public Page<CardDTO> search(Long userId, String query, Pageable pageable) {
        logger.info("Searching cards for userId {} and query {}", userId, query);
        Page<Card> cards = cardRepository.findByUserIdAndLastFour(userId, query, pageable);
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
        Card card = getCardEntityById(id);
        if (cardDto.cardStatus() != null) {
            CardStatus newStatus = CardStatus.valueOf(cardDto.cardStatus());
            switch (newStatus) {
                case BLOCKED -> card.blockCard();
                case ACTIVE -> card.unblockCard();
                case BLOCK_REQUESTED -> card.requestBlock();
                default -> throw new IllegalArgumentException("Cannot directly set card status to: " + newStatus);
            }
        }
        if (cardDto.expirationDate() != null) {
            card.setExpirationDate(YearMonth.parse(cardDto.expirationDate()).atDay(1));
        }
        return cardMapper.toCardDTO(cardRepository.save(card));
    }

    @Override
    public void remove(Long id) {
        logger.info("Removing card with ID {}", id);
        requireCardExists(id);
        cardRepository.deleteById(id);
    }

    private void requireCardExists(Long id) {
        if (!cardRepository.existsById(id)) {
            throw new ResourceNotFoundException("Card not found with id: " + id);
        }
    }

    private Card getCardEntityById(Long cardId) {
        return cardRepository.findById(cardId)
                .orElseThrow(() -> new ResourceNotFoundException("Card not found with id: " + cardId));
    }
}
