package br.com.rmacario.transfer.domain.customer.account.movement;

/**
 * Descreve os erros mapeados que podem ocorrer durante uma solicitação de transferência de fundos.
 *
 * @author rmacario
 */
public enum MovementErrorType {
    INSUFFICIENT_FUNDS,

    SAME_ORIGIN,

    TRANSFER_LIMIT_EXCEEDED,

    CONCURRENT_TRANSFER;
}
