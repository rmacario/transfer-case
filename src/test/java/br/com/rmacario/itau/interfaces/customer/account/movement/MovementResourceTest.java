package br.com.rmacario.itau.interfaces.customer.account.movement;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import br.com.rmacario.itau.application.customer.movement.MovementApplicationService;
import br.com.rmacario.itau.application.customer.movement.TransferFundsSolicitation;
import br.com.rmacario.itau.domain.customer.account.movement.AccountMovement;
import java.math.BigDecimal;
import java.util.List;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;

@ExtendWith(MockitoExtension.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
class MovementResourceTest {

    private static final Long ACCOUNT_ORIGIN = 5l;

    private static final Long ACCOUNT_TARGET = 10l;

    private static final BigDecimal AMOUNT = BigDecimal.TEN;

    private static final Long ACCOUNT_NUMBER = 10l;

    private static final Integer PAGE = 0;

    MovementResource movementResource;

    @Mock MovementApplicationService movementApplicationService;

    @Mock TransferFundsRequest request;

    @Mock AccountMovementDataTranslator translator;

    @Mock AccountMovement accountMovement;

    @BeforeEach
    void setup() {
        this.movementResource = new MovementResource(movementApplicationService, translator);
    }

    @Test
    void transferFunds_parametersOk_shouldRequestTransferFunds() {
        when(request.getAccountOrigin()).thenReturn(ACCOUNT_ORIGIN);
        when(request.getAccountTarget()).thenReturn(ACCOUNT_TARGET);
        when(request.getAmount()).thenReturn(AMOUNT);

        final var response = movementResource.transferFunds(request);

        final var solicitationArgumentCaptor =
                ArgumentCaptor.forClass(TransferFundsSolicitation.class);
        verify(movementApplicationService)
                .requestTransferFunds(solicitationArgumentCaptor.capture());

        assertNotNull(solicitationArgumentCaptor.getValue());
        assertEquals(ACCOUNT_ORIGIN, solicitationArgumentCaptor.getValue().getAccountOrigin());
        assertEquals(ACCOUNT_TARGET, solicitationArgumentCaptor.getValue().getAccountTarget());
        assertEquals(AMOUNT, solicitationArgumentCaptor.getValue().getAmount());
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertTrue(response.getBody().getSuccess());
    }

    @Test
    void findByAccountNumber_parameterOk_shouldReturnPagedAccountMovements() {
        final var pagedAccountMovements =
                new PageImpl<>(List.of(this.accountMovement, accountMovement));
        when(movementApplicationService.findByAccountNumber(ACCOUNT_NUMBER, PAGE))
                .thenReturn(pagedAccountMovements);

        final var response = movementResource.findByAccountNumber(ACCOUNT_NUMBER, PAGE);

        verify(translator, times(2)).toAccountMovementResponse(any());
        assertEquals(2, response.getBody().getTotalElements());
    }
}
