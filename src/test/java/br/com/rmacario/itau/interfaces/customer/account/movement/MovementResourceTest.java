package br.com.rmacario.itau.interfaces.customer.account.movement;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import br.com.rmacario.itau.application.customer.movement.MovementApplicationService;
import br.com.rmacario.itau.application.customer.movement.TransferFundsSolicitation;
import java.math.BigDecimal;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

@ExtendWith(MockitoExtension.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
class MovementResourceTest {

    private static final Long ACCOUNT_ORIGIN = 5l;

    private static final Long ACCOUNT_TARGET = 10l;

    private static final BigDecimal AMOUNT = BigDecimal.TEN;

    MovementResource movementResource;

    @Mock MovementApplicationService movementApplicationService;

    @Mock TransferFundsRequest request;

    @BeforeEach
    void setup() {
        this.movementResource = new MovementResource(movementApplicationService);
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
}
