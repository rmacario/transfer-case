package br.com.rmacario.transfer.application.customer;

import br.com.rmacario.transfer.domain.customer.Customer;

/**
 * Exceção lançada quando ocorre a tentativa de cadastrar um {@link Customer} com um número de conta
 * que já existe.
 *
 * @author rmacario
 */
public class AccountNumberAlreadyExistsException extends RuntimeException {

    private static final long serialVersionUID = -6010043833320778258L;

    AccountNumberAlreadyExistsException(Long accountNumber) {
        super(String.format("Account number [%s] already exists.", accountNumber));
    }
}
