package br.com.rmacario.itau.domain.customer.account.movement;

/**
 * Exceção lançada quando ocorre uma tentativa de transferir fundos de um cliente para ele próprio.
 *
 * @author rmacario
 */
public class TransferFundsToSameOriginException extends MovementBusinessException {

    private static final long serialVersionUID = -3922401745677919449L;

    TransferFundsToSameOriginException() {
        super(MovementErrorType.SAME_ORIGIN, "Conta destino inválida.");
    }
}
