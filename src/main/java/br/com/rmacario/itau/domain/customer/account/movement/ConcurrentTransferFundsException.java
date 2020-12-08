package br.com.rmacario.itau.domain.customer.account.movement;

/**
 * Exceção lançada quando ocorre concorrência entre duas operações de transferência de fundos.
 *
 * @author rmacario
 */
public class ConcurrentTransferFundsException extends RuntimeException {

    private static final long serialVersionUID = -8983954895942434314L;

    ConcurrentTransferFundsException() {
        super("Existe outra operação de transferência de fundos em andamento.");
    }
}
