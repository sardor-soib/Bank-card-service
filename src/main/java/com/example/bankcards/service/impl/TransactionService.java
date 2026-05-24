package com.example.bankcards.service.impl;

import com.example.bankcards.dto.TransactionDTO;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.Transaction;
import com.example.bankcards.exception.ResourceNotFoundException;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.TransactionRepository;
import com.example.bankcards.service.CardManager;
import com.example.bankcards.service.TransactionManager;
import com.example.bankcards.util.Currency;
import com.example.bankcards.util.TransactionMapper;
import com.example.bankcards.util.TransactionStatus;
import com.example.bankcards.util.TransactionType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.math.BigDecimal;
import java.util.Optional;

@Service
@Validated
@Transactional
public class TransactionService implements TransactionManager {

    private static final Logger logger = LoggerFactory.getLogger(TransactionService.class);

    private final TransactionRepository transactionRepository;
    private final TransactionMapper transactionMapper;
    private final CardManager cardManager;
    private final CardRepository cardRepository;


    @Autowired
    public TransactionService(TransactionRepository transactionRepository, TransactionMapper transactionMapper, CardManager cardManager, CardRepository cardRepository) {
        this.transactionRepository = transactionRepository;
        this.transactionMapper = transactionMapper;
        this.cardManager = cardManager;
        this.cardRepository = cardRepository;
    }

    @Override
    public boolean isExists(Long id) {
        logger.info("Checking if transaction exists with {}", id);
        return transactionRepository.existsById(id);
    }

    @Override
    public void transferBetweenUserCards(Long userId, Long sourceCardId, Long targetCardId, BigDecimal amount) {

        logger.info("Transfer between user cards for userId {}, sourceCardId {}, targetCardId {}, amount {}", userId, sourceCardId, targetCardId, amount);

        Optional<Card> sourceCard = cardRepository.findById(sourceCardId);
        Optional<Card> targetCard = cardRepository.findById(targetCardId);

        if (sourceCard.isEmpty() || targetCard.isEmpty()) {
            logger.error("One of cards is not exists");
            throw new ResourceNotFoundException("One of cards is not exists");
        }

        cardManager.transferBalance(userId, sourceCardId, targetCardId, amount);

        Transaction debitTransaction = Transaction.createTransaction(
                sourceCard.get(),
                sourceCard.get().getUser(),
                amount,
                Currency.USD,
                TransactionType.DEBIT,
                TransactionStatus.POSTED
        );

        Transaction creditTransaction = Transaction.createTransaction(
                targetCard.get(),
                targetCard.get().getUser(),
                amount,
                Currency.USD,
                TransactionType.CREDIT,
                TransactionStatus.POSTED
        );

        transactionRepository.save(creditTransaction);
        transactionRepository.save(debitTransaction);
    }

    @Override
    public TransactionDTO create(TransactionDTO transactionDTO) {
        logger.info("Creating transaction for transactionDTO {}", transactionDTO);
        Transaction transaction = transactionMapper.toTransaction(transactionDTO);
        return transactionMapper.toTransactionDTO(transactionRepository.save(transaction));
    }

    @Override
    public TransactionDTO findById(Long id) {
        logger.info("Finding transaction by id {}", id);
        return transactionMapper.toTransactionDTO(transactionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction with ID %d not found".formatted(id))));
    }

    @Override
    public Page<TransactionDTO> findByUserId(Long userId, Pageable pageable) {
        logger.info("Finding transactions by userId {}", userId);
        return transactionMapper.toTransactionDTOPage(transactionRepository.findByUserId(userId, pageable));
    }

    @Override
    public Page<TransactionDTO> findByCardId(Long cardId, Pageable pageable) {
        logger.info("Finding transactions by cardId {}", cardId);
        return transactionMapper.toTransactionDTOPage(transactionRepository.findByCardId(cardId, pageable));
    }

    @Override
    public Page<TransactionDTO> findAll(Pageable pageable) {
        logger.info("Finding all transactions");
        return transactionMapper.toTransactionDTOPage(transactionRepository.findAll(pageable));
    }

    @Override
    public TransactionDTO update(Long id, TransactionDTO transactionDTO) {
        logger.info("Updating transaction for transactionDTO {}", transactionDTO);
        if (!isExists(id)) {
            logger.error("Transaction with ID {} not exists", id);
            throw new ResourceNotFoundException("Transaction with ID %d not found".formatted(id));
        }
        Transaction transaction = transactionMapper.toTransaction(transactionDTO);
        return transactionMapper.toTransactionDTO(transactionRepository.save(transaction));
    }

    @Override
    public void remove(Long id) {
        logger.info("Removing transaction with id {}", id);
        if (!isExists(id)) {
            logger.error("Transaction with ID {} not exists", id);
            throw new ResourceNotFoundException("Transaction with ID %d not found".formatted(id));
        }
        transactionRepository.deleteById(id);
    }
}
