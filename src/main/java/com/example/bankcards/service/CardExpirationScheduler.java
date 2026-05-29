package com.example.bankcards.service;

import com.example.bankcards.entity.Card;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.util.CardStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Component
public class CardExpirationScheduler {

    private static final Logger logger = LoggerFactory.getLogger(CardExpirationScheduler.class);

    private final CardRepository cardRepository;

    public CardExpirationScheduler(CardRepository cardRepository) {
        this.cardRepository = cardRepository;
    }

    @Scheduled(cron = "0 0 1 * * *")
    @Transactional
    public void expireCards() {
        List<Card> expired = cardRepository.findByCardStatusAndExpirationDateBefore(
                CardStatus.ACTIVE, LocalDate.now());

        if (!expired.isEmpty()) {
            logger.info("Expiring {} card(s)", expired.size());
            expired.forEach(card -> card.setCardStatus(CardStatus.EXPIRED));
            cardRepository.saveAll(expired);
        }
    }
}