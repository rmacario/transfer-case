package br.com.rmacario.itau.domain.customer.account.movement;

import static br.com.rmacario.itau.domain.customer.account.movement.MovementErrorType.CONCURRENT_TRANSFER;

/**
 * Exceção lançada quando ocorre concorrência entre duas operações de transferência de fundos.
 *
 * @author rmacario
 */
public class ConcurrentTransferFundsException extends MovementBusinessException {

    private static final long serialVersionUID = -8983954895942434314L;

    ConcurrentTransferFundsException() {
        super(
                CONCURRENT_TRANSFER,
                "Existe outra operação de transferência de fundos em andamento.");
    }
}
