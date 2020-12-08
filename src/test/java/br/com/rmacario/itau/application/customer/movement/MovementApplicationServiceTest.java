package br.com.rmacario.itau.application.customer.movement;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import br.com.rmacario.itau.domain.customer.Customer;
import br.com.rmacario.itau.domain.customer.CustomerRepository;
import br.com.rmacario.itau.domain.customer.account.Account;
import br.com.rmacario.itau.domain.customer.account.movement.AccountMovement;
import br.com.rmacario.itau.domain.customer.account.movement.MovementDomainService;
import br.com.rmacario.itau.domain.customer.account.movement.MovementType;
import java.math.BigDecimal;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
class MovementApplicationServiceTest {

    private static final Long ACCOUNT_ORIGIN = 5l;

    private static final Long ACCOUNT_TARGET = 10l;

    private static final BigDecimal AMOUNT = BigDecimal.TEN;

    MovementApplicationService movementApplicationService;

    @Mock MovementDomainService movementDomainService;

    @Mock CustomerRepository customerRepository;

    @Mock Customer customerOrigin;

    @Mock Customer customerTarget;

    @Mock Account account;

    @Mock Account accountTarget;

    @Mock TransferFundsSolicitation transferFundsSolicitation;

    @BeforeEach
    void setup() {
        this.movementApplicationService =
                new MovementApplicationService(movementDomainService, customerRepository);
    }

    @Test
    void requestTransferFunds_parameterNull_shouldThrowIllegalArgumentException() {
        assertThrows(
                IllegalArgumentException.class,
                () -> movementApplicationService.requestTransferFunds(null));
    }

    @Test
    void requestTransferFunds_parametersOk_shouldInvokeDomainTransferFunds() {
        when(transferFundsSolicitation.getAccountOrigin()).thenReturn(ACCOUNT_ORIGIN);
        when(transferFundsSolicitation.getAccountTarget()).thenReturn(ACCOUNT_TARGET);
        when(transferFundsSolicitation.getAmount()).thenReturn(AMOUNT);
        when(customerRepository.findOneByAccountNumber(ACCOUNT_ORIGIN)).thenReturn(customerOrigin);
        when(customerRepository.findOneByAccountNumber(ACCOUNT_TARGET)).thenReturn(customerTarget);
        when(customerOrigin.getAccount()).thenReturn(account);
        when(customerTarget.getAccount()).thenReturn(accountTarget);

        movementApplicationService.requestTransferFunds(transferFundsSolicitation);

        final var accountMovementCaptured = ArgumentCaptor.forClass(AccountMovement.class);
        verify(movementDomainService).transferFunds(accountMovementCaptured.capture());

        assertNotNull(accountMovementCaptured.getValue());
        assertEquals(AMOUNT, accountMovementCaptured.getValue().getValue());
        assertEquals(account, accountMovementCaptured.getValue().getAccount());
        assertEquals(account, accountMovementCaptured.getValue().getAccountOrigin());
        assertEquals(accountTarget, accountMovementCaptured.getValue().getAccountTarget());
        assertEquals(MovementType.TRANSFER, accountMovementCaptured.getValue().getType());
    }
}
