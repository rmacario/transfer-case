package br.com.rmacario.itau.domain.customer.account.movement;

import br.com.rmacario.itau.domain.customer.Customer;
import br.com.rmacario.itau.domain.customer.account.Account;
import br.com.rmacario.itau.domain.customer.account.AccountRepository;
import java.math.BigDecimal;
import java.util.function.Supplier;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Serviço que encapsula as regras de negócio de operações que envolvem movimentações financeiras.
 *
 * @author rmacario
 */
@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MovementDomainService {

    AccountRepository accountRepository;

    AccountMovementRepository accountMovementRepository;

    BigDecimal transferLimitAmount;

    @Autowired
    MovementDomainService(
            final AccountRepository accountRepository,
            final AccountMovementRepository accountMovementRepository,
            @Value("${app.domain.movements.transfer-limit-amount}")
                    final BigDecimal transferLimitAmount) {
        this.accountRepository = accountRepository;
        this.accountMovementRepository = accountMovementRepository;
        this.transferLimitAmount = transferLimitAmount;
    }

    /**
     * Realiza a transferência de fundos entre as contas informadas.
     *
     * <p>Para que a transferência seja feita com sucesso é necessário que:
     *
     * <ul>
     *   <li>Exista uma conta cadastrada para os usuários envolvidos;
     *   <li>Não haja outra movimentação em andamento;
     *   <li>O usuário que está realizando a transferência tenha saldo suficiente;
     * </ul>
     *
     * @param accountMovement dados da movimentação que será realizada.
     * @throws TransferFundsToSameOriginException se a movimentação for realizada de um usuário para
     *     ele próprio.
     * @throws InsufficientBalanceException se o usuário não possuir saldo suficiente.
     * @throws ConcurrentTransferFundsException se houver outra movimentação em andamento para as
     *     contas envolvidas.
     */
    @Transactional(
            noRollbackFor = {
                TransferFundsToSameOriginException.class,
                InsufficientBalanceException.class,
                TransferLimitExceededException.class
            })
    public void transferFunds(@NonNull final AccountMovement accountMovement) {
        final var accountOriginId = accountMovement.getAccount().getId();
        final var accountTargetId = accountMovement.getAccountTarget().getId();
        final var accountOrigin = accountRepository.findByIdAndLockEntity(accountOriginId);
        final var accountTarget = accountRepository.findByIdAndLockEntity(accountTargetId);

        if (accountOrigin.getId().equals(accountTarget.getId())) {
            saveAsFailAndThrowError(
                    accountMovement, () -> new TransferFundsToSameOriginException(accountOriginId));

        } else if (transferAmountExceedLimit(accountMovement.getValue())) {
            saveAsFailAndThrowError(
                    accountMovement, () -> new TransferLimitExceededException(transferLimitAmount));

        } else if (accountHasNotSufficientBalance(accountMovement, accountOrigin)) {
            saveAsFailAndThrowError(accountMovement, InsufficientBalanceException::new);
        }

        sendFunds(accountMovement, accountOrigin);
        receiveFunds(accountMovement, accountTarget);
    }

    // --------------------------------
    // Privates

    /**
     * Persiste a movimentação através de uma nova transação para que haja o histórico de tentativas
     * de transferências, independente de haver sucesso ou não.
     */
    private void saveAsFailAndThrowError(
            final AccountMovement accountMovement, final Supplier<MovementBusinessException> ex) {
        accountMovement.setSuccess(false);
        accountMovementRepository.saveOnNewTransaction(accountMovement);
        throw ex.get();
    }

    /** Indica se o valor que está sendo transferido ultrapassa o limite permitido. */
    private boolean transferAmountExceedLimit(final BigDecimal transferAmount) {
        return transferAmount.compareTo(transferLimitAmount) > 0;
    }

    /** Indica se existe saldo suficiente em conta para realizar a movimentação. */
    private boolean accountHasNotSufficientBalance(
            final AccountMovement accountMovement, final Account account) {
        return account.getBalance().compareTo(accountMovement.getValue()) < 0;
    }

    /**
     * Recalcula o saldo em conta do {@link Customer} que está realizando a transferência de fundos
     * e adiciona um {@link AccountMovement} ao histórico de movimentações da conta do mesmo.
     */
    private void sendFunds(final AccountMovement accountMovement, final Account account) {
        account.subtractBalance(accountMovement.getValue());
        account.getAccountMovements().add(accountMovement);
        persistHandlingLockException(account, accountMovement);
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
        persistHandlingLockException(account, accountMovement);
    }

    /**
     * Tenta atualizar a {@link Account} e, em caso de erro, persiste ao menos a tentativa de
     * movimentação financeira em uma transação separada.
     */
    private void persistHandlingLockException(
            final Account account, final AccountMovement currentAccountMovement) {
        try {
            accountRepository.saveAndFlush(account);

        } catch (final Exception e) {
            currentAccountMovement.setSuccess(false);
            accountMovementRepository.saveOnNewTransaction(currentAccountMovement);

            if (e instanceof ObjectOptimisticLockingFailureException) {
                throw new ConcurrentTransferFundsException();
            }

            throw e;
        }
    }
}
