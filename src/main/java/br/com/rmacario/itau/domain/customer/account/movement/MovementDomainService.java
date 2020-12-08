package br.com.rmacario.itau.domain.customer.account.movement;

import br.com.rmacario.itau.domain.customer.Customer;
import br.com.rmacario.itau.domain.customer.account.Account;
import br.com.rmacario.itau.domain.customer.account.AccountRepository;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Serviço que encapsula as regras de negócio de operações que envolvem movimentações financeiras.
 *
 * @author rmacario
 */
@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor(access = AccessLevel.PROTECTED, onConstructor_ = @Autowired)
public class MovementDomainService {

    AccountRepository accountRepository;

    AccountMovementRepository accountMovementRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void transferFunds(@NonNull final AccountMovement accountMovement) {
        final var accountOrigin =
                accountRepository.findByIdAndLockEntity(accountMovement.getAccount().getId());
        final var accountTarget =
                accountRepository.findByIdAndLockEntity(accountMovement.getAccountTarget().getId());

        if (hasSufficientFunds(accountMovement, accountOrigin)) {
            sendFunds(accountMovement, accountOrigin);
            receiveFunds(accountMovement, accountTarget);

        } else {
            accountMovement.setSuccess(false);
            accountMovementRepository.saveAndFlush(accountMovement);
            throw new InsufficientBalanceException();
        }
    }

    // --------------------------------
    // Privates

    /** Indica se existe saldo suficiente em conta para realizar a movimentação. */
    private boolean hasSufficientFunds(
            final AccountMovement accountMovement, final Account account) {
        return account.getBalance().compareTo(accountMovement.getValue()) >= 0;
    }

    /**
     * Recalcula o saldo em conta do {@link Customer} que está transferindo os valores e adiciona um
     * {@link AccountMovement} ao histórico de movimentações da conta do mesmo.
     */
    private void sendFunds(final AccountMovement accountMovement, final Account account) {
        account.subtractBalance(accountMovement.getValue());
        account.getAccountMovements().add(accountMovement);
        accountRepository.save(account);
    }

    /**
     * Recalcula o saldo em conta do {@link Customer} que está recebendo os valores e adiciona um
     * {@link AccountMovement} ao histórico de movimentações da conta do mesmo.
     */
    private void receiveFunds(final AccountMovement accountMovement, final Account account) {
        account.addBalance(accountMovement.getValue());
        account.getAccountMovements()
                .add(
                        AccountMovement.builder()
                                .value(accountMovement.getValue())
                                .account(account)
                                .accountOrigin(accountMovement.getAccountOrigin())
                                .accountTarget(accountMovement.getAccountTarget())
                                .type(MovementType.RECEIVEMENT)
                                .build());
        accountRepository.save(account);
    }
}
