package br.com.rmacario.itau.domain.customer.account.movement;

/**
 * Exceção lançada quando ocorre uma tentativa de transferência de fundos mas a conta do solicitante
 * não possui saldo suficiente.
 *
 * @author rmacario
 */
public class InsufficientBalanceException extends RuntimeException {

    private static final long serialVersionUID = -5671305696516082599L;

    InsufficientBalanceException() {
        super("Saldo insuficiente.");
    }
}
