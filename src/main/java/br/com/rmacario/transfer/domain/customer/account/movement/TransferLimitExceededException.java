package br.com.rmacario.transfer.domain.customer.account.movement;

import static br.com.rmacario.transfer.domain.customer.account.movement.MovementErrorType.TRANSFER_LIMIT_EXCEEDED;

import java.math.BigDecimal;

/**
 * Exceção lançada quando ocorre uma tentativa de transferência de fundos em um valor maior do que o
 * permitido.
 *
 * @author rmacario
 */
public class TransferLimitExceededException extends MovementBusinessException {

    private static final long serialVersionUID = 7646007436040707113L;

    TransferLimitExceededException(final BigDecimal limitAmount) {
        super(
                TRANSFER_LIMIT_EXCEEDED,
                String.format(
                        "O valor da transferência não pode ultrapassar o limite de R$ %s.",
                        limitAmount.setScale(2)));
    }
}
