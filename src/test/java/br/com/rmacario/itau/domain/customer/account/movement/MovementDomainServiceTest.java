package br.com.rmacario.itau.domain.customer.account.movement;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import br.com.rmacario.itau.domain.customer.account.Account;
import br.com.rmacario.itau.domain.customer.account.AccountRepository;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.stream.Stream;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
class MovementDomainServiceTest {

    private static final Long ACCOUNT_ORIGIN_ID = 10l;

    private static final Long ACCOUNT_TARGET_ID = 20l;

    private static final BigDecimal TRANSFER_AMOUNT = BigDecimal.TEN;

    MovementDomainService movementDomainService;

    AccountMovement accountMovement;

    @Mock AccountRepository accountRepository;

    @Mock AccountMovementRepository accountMovementRepository;

    @Mock Account accountOrigin;

    @Mock Account accountTarget;

    @BeforeEach
    void setup() {
        this.movementDomainService =
                new MovementDomainService(accountRepository, accountMovementRepository);
        this.accountMovement =
                AccountMovement.builder()
                        .type(MovementType.TRANSFER)
                        .account(accountOrigin)
                        .accountOrigin(accountOrigin)
                        .accountTarget(accountTarget)
                        .value(TRANSFER_AMOUNT)
                        .build();
    }

    @ParameterizedTest
    @MethodSource("getMovementArguments")
    void transferFunds_parametrized(
            final boolean areAccountsOriginAndTargetEquals,
            final boolean hasSufficientFunds,
            final Class<?> expectedExceptionClass) {
        when(accountOrigin.getId()).thenReturn(ACCOUNT_ORIGIN_ID);
        when(accountTarget.getId()).thenReturn(ACCOUNT_TARGET_ID);
        when(accountRepository.findByIdAndLockEntity(any()))
                .thenReturn(accountOrigin, accountTarget);

        if (areAccountsOriginAndTargetEquals || !hasSufficientFunds) {
            final long accountTargetId =
                    areAccountsOriginAndTargetEquals ? ACCOUNT_ORIGIN_ID : ACCOUNT_TARGET_ID;
            lenient().when(accountOrigin.getId()).thenReturn(ACCOUNT_ORIGIN_ID);
            lenient().when(accountTarget.getId()).thenReturn(accountTargetId);
            lenient().when(accountOrigin.getBalance()).thenReturn(BigDecimal.ZERO);

            final var accountMovementCaptor = ArgumentCaptor.forClass(AccountMovement.class);
            try {
                movementDomainService.transferFunds(accountMovement);

            } catch (RuntimeException e) {
                assertEquals(e.getClass(), expectedExceptionClass);
                verify(accountMovementRepository).saveAndFlush(accountMovementCaptor.capture());
                assertNotNull(accountMovementCaptor.getValue());
                assertFalse(accountMovementCaptor.getValue().getSuccess());
            }

        } else {
            when(accountOrigin.getBalance()).thenReturn(TRANSFER_AMOUNT);
            final var accountMovementsOrigin = spy(new ArrayList<AccountMovement>());
            final var accountMovementsTarget = spy(new ArrayList<AccountMovement>());
            when(accountOrigin.getAccountMovements()).thenReturn(accountMovementsOrigin);
            when(accountTarget.getAccountMovements()).thenReturn(accountMovementsTarget);

            final var accountCaptor = ArgumentCaptor.forClass(Account.class);
            movementDomainService.transferFunds(accountMovement);
            verify(accountRepository, times(2)).save(accountCaptor.capture());

            // Validando movimentações do usuário que está transferindo
            verify(accountOrigin).subtractBalance(accountMovement.getValue());
            verify(accountMovementsOrigin).add(any());

            // Validando movimentações do usuário que está recebendo
            verify(accountTarget).addBalance(accountMovement.getValue());
            verify(accountMovementsTarget).add(any());
            final var capturedValues = accountCaptor.getAllValues();
            assertEquals(2, capturedValues.size());
            assertEquals(1, capturedValues.get(1).getAccountMovements().size());
            final var receivementMovement = capturedValues.get(1).getAccountMovements().get(0);
            assertEquals(TRANSFER_AMOUNT, receivementMovement.getValue());
            assertEquals(accountTarget, receivementMovement.getAccount());
            assertEquals(accountOrigin, receivementMovement.getAccountOrigin());
            assertEquals(accountTarget, receivementMovement.getAccountTarget());
            assertEquals(MovementType.RECEIVEMENT, receivementMovement.getType());
        }
    }

    static Stream<Arguments> getMovementArguments() {
        return Stream.of(
                Arguments.of(true, false, TransferFundsToSameOriginException.class),
                Arguments.of(false, false, InsufficientBalanceException.class),
                Arguments.of(false, true, null));
    }
}
