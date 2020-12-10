package br.com.rmacario.transfer.domain.customer.account.movement;

/**
 * Descreve os tipos de movimentações financeiras possíveis de serem realizadas.
 *
 * @author rmacario
 */
public enum MovementType {

    /**
     * Indica uma movimentação do tipo transferência, onde os valores estão saindo da conta do
     * cliente.
     */
    TRANSFER,

    /**
     * Indica uma movimentação do tipo recebimento, onde os valore estão entrando na conta do
     * cliente.
     */
    RECEIVEMENT
}
