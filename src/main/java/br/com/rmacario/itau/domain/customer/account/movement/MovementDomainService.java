package br.com.rmacario.itau.domain.customer.account.movement;

import br.com.rmacario.itau.domain.customer.Customer;
import br.com.rmacario.itau.domain.customer.account.Account;
import br.com.rmacario.itau.domain.customer.account.AccountRepository;
import java.math.BigDecimal;
import java.util.function.Function;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.PessimisticLockingFailureException;
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
public class MovementDomainService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MovementDomainService.class);

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
            propagation = Propagation.REQUIRES_NEW,
            noRollbackFor = {
                ConcurrentTransferFundsException.class,
                TransferFundsToSameOriginException.class,
                InsufficientBalanceException.class,
                TransferLimitExceededException.class
            })
    public void transferFunds(@NonNull final AccountMovement accountMovement) {
        final var accountOriginId = accountMovement.getAccount().getId();
        final var accountTargetId = accountMovement.getAccountTarget().getId();
        final var accountOrigin = getLockedAccountHandlingError(accountOriginId, accountMovement);
        final var accountTarget = getLockedAccountHandlingError(accountTargetId, accountMovement);

        if (accountOrigin.getId().equals(accountTarget.getId())) {
            saveAsUnsuccessAndThrowError(
                    accountMovement,
                    (am) -> {
                        LOGGER.error(
                                "msg=Trying to transfer funds to same orgin, accountId={}.",
                                accountOriginId);
                        throw new TransferFundsToSameOriginException();
                    });

        } else if (transferAmountExceedLimit(accountMovement.getValue())) {
            saveAsUnsuccessAndThrowError(
                    accountMovement,
                    (am) -> {
                        LOGGER.error(
                                "msg=Trying to transfer amount greather than allowed, transferValue={}.",
                                accountMovement.getValue());
                        throw new TransferLimitExceededException(transferLimitAmount);
                    });
        } else if (accountHasNotSufficientFunds(accountMovement, accountOrigin)) {
            saveAsUnsuccessAndThrowError(
                    accountMovement,
                    (am) -> {
                        LOGGER.error("msg=Customer has not balance enough.");
                        throw new InsufficientBalanceException();
                    });
        }

        sendFunds(accountMovement, accountOrigin);
        receiveFunds(accountMovement, accountTarget);
    }

    // --------------------------------
    // Privates

    private void saveAsUnsuccessAndThrowError(
            final AccountMovement accountMovement, final Function<AccountMovement, Void> ex) {
        accountMovement.setSuccess(false);
        accountMovementRepository.save(accountMovement);
        ex.apply(accountMovement);
    }

    /**
     * Obtém um registro de {@link Account} buscando pelo id informado e realizando um lock no
     * mesmo.
     *
     * <p<Caso o lock não seja feito com sucesso, será lançado uma exceção do tipo {@link ConcurrentTransferFundsException}.</p>
     */
    private Account getLockedAccountHandlingError(
            final Long accountId, final AccountMovement accountMovement) {
        try {
            return accountRepository.findByIdAndLockEntity(accountId);

        } catch (final PessimisticLockingFailureException e) {
            saveAsUnsuccessAndThrowError(
                    accountMovement,
                    (am) -> {
                        LOGGER.error("msg=Fail to lock account, accountId=[].", accountId);
                        throw new ConcurrentTransferFundsException();
                    });
            return null;
        }
    }

    /** Indica se o valor que está sendo transferido ultrapassa o limite permitido. */
    private boolean transferAmountExceedLimit(final BigDecimal transferAmount) {
        return transferAmount.compareTo(transferLimitAmount) > 0;
    }

    /** Indica se existe saldo suficiente em conta para realizar a movimentação. */
    private boolean accountHasNotSufficientFunds(
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
