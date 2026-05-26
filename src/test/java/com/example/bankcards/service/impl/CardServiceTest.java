package com.example.bankcards.service.impl;

import com.example.bankcards.dto.CardDTO;
import com.example.bankcards.dto.CreateCardDTO;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.User;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.security.PanHashEncoder;
import com.example.bankcards.service.TransactionManager;
import com.example.bankcards.util.CardMapper;
import com.example.bankcards.util.CardStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.ThrowableAssert.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class CardServiceTest {

    @Mock
    CardRepository cardRepository;
    @Mock
    CardMapper cardMapper;
    @Mock
    PanHashEncoder panHashEncoder;
    @Mock
    TransactionManager transactionManager;

    @InjectMocks
    CardService cardService;

    private Card sourceCard;
    private Card targetCard;
    private User owner;

    @BeforeEach
    void setUp() {
        owner = org.mockito.Mockito.mock(User.class);
        when(owner.getId()).thenReturn(1L);

        sourceCard = org.mockito.Mockito.mock(Card.class);
        when(sourceCard.getId()).thenReturn(10L);
        when(sourceCard.getUser()).thenReturn(owner);
        when(sourceCard.getCardStatus()).thenReturn(CardStatus.ACTIVE);
        when(sourceCard.getBalance()).thenReturn(new BigDecimal("100.00"));

        targetCard = org.mockito.Mockito.mock(Card.class);
        when(targetCard.getId()).thenReturn(20L);
        when(targetCard.getUser()).thenReturn(owner);
        when(targetCard.getCardStatus()).thenReturn(CardStatus.ACTIVE);
        when(targetCard.getBalance()).thenReturn(BigDecimal.ZERO);
    }

    @Test
    void isExists_delegatesToRepository() {
        when(cardRepository.existsById(5L)).thenReturn(true);

        assertThat(cardService.isExists(5L)).isTrue();
    }

    @Test
    void transferBalance_happyPath_movesFundsAndFormsTransactions() {
        when(cardRepository.findById(10L)).thenReturn(Optional.of(sourceCard));
        when(cardRepository.findById(20L)).thenReturn(Optional.of(targetCard));

        cardService.transferBalance(1L, 10L, 20L, new BigDecimal("25.00"));

        verify(sourceCard).debit(new BigDecimal("25.00"));
        verify(targetCard).credit(new BigDecimal("25.00"));
        verify(cardRepository).save(sourceCard);
        verify(cardRepository).save(targetCard);
        verify(transactionManager).formTransactions(sourceCard, targetCard, new BigDecimal("25.00"));
    }

    @Test
    void transferBalance_sourceMissing_throwsNotFound() {
        when(cardRepository.findById(10L)).thenReturn(Optional.empty());

        Throwable thrown = catchThrowable(() ->
                cardService.transferBalance(1L, 10L, 20L, new BigDecimal("1"))
        );
        assertThat(thrown).as("Expected ResourceNotFoundException for missing source card").isInstanceOf(ResourceNotFoundException.class);

        verify(transactionManager, never()).formTransactions(any(), any(), any());
    }

    @Test
    void transferBalance_validationFailure_propagatesAndSkipsSave() {
        when(cardRepository.findById(10L)).thenReturn(Optional.of(sourceCard));
        when(cardRepository.findById(20L)).thenReturn(Optional.of(targetCard));

        Throwable thrown = catchThrowable(() ->
                cardService.transferBalance(1L, 10L, 20L, new BigDecimal("1000")));

        assertThat(thrown).as("Expected IllegalArgumentException for insufficient funds")
                .isInstanceOf(IllegalArgumentException.class).hasMessageContaining("Insufficient funds");

        verify(cardRepository, never()).save(any());
        verify(transactionManager, never()).formTransactions(any(), any(), any());
    }

    @Test
    void getBalance_ownerMatches_returnsBalance() {
        when(cardRepository.findById(10L)).thenReturn(Optional.of(sourceCard));

        BigDecimal balance = cardService.getBalance(1L, 10L);

        assertThat(balance).isEqualByComparingTo("100.00");
    }

    @Test
    void getBalance_nonOwner_throws() {
        when(cardRepository.findById(10L)).thenReturn(Optional.of(sourceCard));

        assertThatThrownBy(() -> cardService.getBalance(999L, 10L))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void activateCard_unblocksAndSaves() {
        when(cardRepository.findById(10L)).thenReturn(Optional.of(sourceCard));

        cardService.activateCard(10L);

        verify(sourceCard).unblockCard();
        verify(cardRepository).save(sourceCard);
    }

    @Test
    void deactivateCard_blocksAndSaves() {
        when(cardRepository.findById(10L)).thenReturn(Optional.of(sourceCard));

        cardService.deactivateCard(10L);

        verify(sourceCard).blockCard();
        verify(cardRepository).save(sourceCard);
    }

    @Test
    void requestCardBlock_ownerMatches_marksRequestedAndSaves() {
        when(cardRepository.findById(10L)).thenReturn(Optional.of(sourceCard));

        cardService.requestCardBlock(1L, 10L);

        verify(sourceCard).requestBlock();
        verify(cardRepository).save(sourceCard);
    }

    @Test
    void requestCardBlock_nonOwner_throwsAndSkipsSave() {
        when(cardRepository.findById(10L)).thenReturn(Optional.of(sourceCard));

        Throwable throwable = catchThrowable(() -> cardService.requestCardBlock(999L, 10L));

        assertThat(throwable).as("Expected IllegalArgumentException for non-owner")
                .isInstanceOf(IllegalArgumentException.class);

        verify(sourceCard, never()).requestBlock();
        verify(cardRepository, never()).save(any());
    }

    @Test
    void create_appliesPanDataAndSaves() {
        CreateCardDTO dto = new CreateCardDTO("4111111111111111", "John", "2030-01-01",
                "VISA", "DEBIT", new BigDecimal("0"), "ACTIVE", 1L);
        Card mappedCard = org.mockito.Mockito.mock(Card.class);
        Card savedCard = org.mockito.Mockito.mock(Card.class);
        CardDTO returnedDto = CardDTO.builder().id(42L).build();

        when(cardMapper.toCard(dto)).thenReturn(mappedCard);
        when(panHashEncoder.encode("4111111111111111")).thenReturn("hash-value");
        when(cardRepository.save(mappedCard)).thenReturn(savedCard);
        when(cardMapper.toCardDTO(savedCard)).thenReturn(returnedDto);

        CardDTO result = cardService.create(dto);

        verify(mappedCard).applyPanData("411111", "1111", "**** **** **** 1111", "hash-value");
        assertThat(result).isSameAs(returnedDto);
    }

    @Test
    void findById_returnsMappedDto() {
        CardDTO dto = CardDTO.builder().id(10L).build();
        when(cardRepository.findById(10L)).thenReturn(Optional.of(sourceCard));
        when(cardMapper.toCardDTO(sourceCard)).thenReturn(dto);

        assertThat(cardService.findById(10L)).isSameAs(dto);
    }

    @Test
    void findById_missing_throws() {
        when(cardRepository.findById(404L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> cardService.findById(404L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void findByUserId_returnsMappedPage() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Card> page = new PageImpl<>(List.of(sourceCard));
        Page<CardDTO> dtoPage = new PageImpl<>(List.of(CardDTO.builder().id(10L).build()));

        when(cardRepository.findByUserId(1L, pageable)).thenReturn(page);
        when(cardMapper.toCardDTOPage(page)).thenReturn(dtoPage);

        assertThat(cardService.findByUserId(1L, pageable)).isSameAs(dtoPage);
    }

    @Test
    void searchByQuery_delegatesToRepository() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Card> page = new PageImpl<>(List.of(sourceCard));
        Page<CardDTO> dtoPage = new PageImpl<>(List.of());

        when(cardRepository.findByLastFour("1111", pageable)).thenReturn(page);
        when(cardMapper.toCardDTOPage(page)).thenReturn(dtoPage);

        assertThat(cardService.search("1111", pageable)).isSameAs(dtoPage);
    }

    @Test
    void searchByUserAndQuery_delegatesToRepository() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Card> page = new PageImpl<>(List.of(sourceCard));
        Page<CardDTO> dtoPage = new PageImpl<>(List.of());

        when(cardRepository.findByUserIdAndLastFour(1L, "1111", pageable)).thenReturn(page);
        when(cardMapper.toCardDTOPage(page)).thenReturn(dtoPage);

        assertThat(cardService.search(1L, "1111", pageable)).isSameAs(dtoPage);
    }

    @Test
    void findAll_delegatesToRepository() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Card> page = new PageImpl<>(List.of(sourceCard));
        Page<CardDTO> dtoPage = new PageImpl<>(List.of());

        when(cardRepository.findAll(pageable)).thenReturn(page);
        when(cardMapper.toCardDTOPage(page)).thenReturn(dtoPage);

        assertThat(cardService.findAll(pageable)).isSameAs(dtoPage);
    }

    @Test
    void update_existingCard_saves() {
        CardDTO dto = CardDTO.builder().id(10L).build();
        Card mapped = org.mockito.Mockito.mock(Card.class);
        Card saved = org.mockito.Mockito.mock(Card.class);
        CardDTO returned = CardDTO.builder().id(10L).build();

        when(cardRepository.existsById(10L)).thenReturn(true);
        when(cardMapper.toCard(dto)).thenReturn(mapped);
        when(cardRepository.save(mapped)).thenReturn(saved);
        when(cardMapper.toCardDTO(saved)).thenReturn(returned);

        assertThat(cardService.update(10L, dto)).isSameAs(returned);
    }

    @Test
    void update_missingCard_throws() {
        when(cardRepository.existsById(404L)).thenReturn(false);

        Throwable thrown = catchThrowable(() -> cardService.update(404L, CardDTO.builder().id(404L).build()));

        assertThat(thrown).as("Expected ResourceNotFoundException for missing card").isInstanceOf(ResourceNotFoundException.class);

        verify(cardRepository, never()).save(any());
    }

    @Test
    void remove_existing_deletes() {
        when(cardRepository.existsById(10L)).thenReturn(true);

        cardService.remove(10L);

        ArgumentCaptor<Long> idCaptor = ArgumentCaptor.forClass(Long.class);
        verify(cardRepository).deleteById(idCaptor.capture());
        assertThat(idCaptor.getValue()).isEqualTo(10L);
    }

    @Test
    void remove_missing_throws() {
        when(cardRepository.existsById(404L)).thenReturn(false);

        assertThatThrownBy(() -> cardService.remove(404L))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(cardRepository, never()).deleteById(404L);
    }
}
