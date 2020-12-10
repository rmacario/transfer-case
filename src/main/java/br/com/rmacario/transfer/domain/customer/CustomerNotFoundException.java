package br.com.rmacario.transfer.domain.customer.account;

import br.com.rmacario.transfer.domain.customer.Customer;
import javax.persistence.EntityNotFoundException;

/** Exceção lançada quando a busca por um {@link Customer} não encontra registros. */
public class CustomerNotFoundException extends EntityNotFoundException {

    private static final long serialVersionUID = 4800766005696473587L;

    public CustomerNotFoundException(Long accountNumber) {
        super(String.format("Nenhum customer encontrado com número de conta [%s].", accountNumber));
    }
}
