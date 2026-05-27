package com.example.bankcards.service.impl;

import com.example.bankcards.dto.TransactionDTO;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.Transaction;
import com.example.bankcards.repository.TransactionRepository;
import com.example.bankcards.service.TransactionService;
import com.example.bankcards.util.Currency;
import com.example.bankcards.util.TransactionStatus;
import com.example.bankcards.util.TransactionType;
import com.example.bankcards.util.mapper.TransactionMapper;
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

@Service
@Validated
@Transactional
public class TransactionServiceImpl implements TransactionService {

    private static final Logger logger = LoggerFactory.getLogger(TransactionServiceImpl.class);

    private final TransactionRepository transactionRepository;
    private final TransactionMapper transactionMapper;


    @Autowired
    public TransactionServiceImpl(TransactionRepository transactionRepository, TransactionMapper transactionMapper) {
        this.transactionRepository = transactionRepository;
        this.transactionMapper = transactionMapper;
    }

    @Override
    public boolean isExists(Long id) {
        logger.info("Checking if transaction exists with {}", id);
        return transactionRepository.existsById(id);
    }

    @Override
    public void formTransactions(Card sourceCard, Card targetCard, BigDecimal amount) {

        logger.info("Forming transactions for sourceCard {}, targetCard {}, amount {}", sourceCard, targetCard, amount);

        Transaction debitTransaction = Transaction.createTransaction(
                sourceCard,
                sourceCard.getUser(),
                amount,
                Currency.USD,
                TransactionType.DEBIT,
                TransactionStatus.POSTED
        );

        Transaction creditTransaction = Transaction.createTransaction(
                targetCard,
                targetCard.getUser(),
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
        requireTransactionExists(id);
        Transaction transaction = transactionMapper.toTransaction(transactionDTO);
        return transactionMapper.toTransactionDTO(transactionRepository.save(transaction));
    }

    @Override
    public void remove(Long id) {
        logger.info("Removing transaction with id {}", id);
        requireTransactionExists(id);
        transactionRepository.deleteById(id);
    }

    private void requireTransactionExists(Long id) {
        if (!transactionRepository.existsById(id)) {
            throw new ResourceNotFoundException("Transaction not found with id: " + id);
        }
    }
}
