package br.com.rmacario.itau.interfaces.customer;

import br.com.rmacario.itau.application.customer.CustomerCreateSolicitation;
import br.com.rmacario.itau.domain.customer.Customer;
import lombok.NonNull;
import org.springframework.stereotype.Component;

/**
 * Classe utilitária responsável por converter objetos de requisição expostos aos clients em objetos
 * utilizados internamente pela aplicação.
 *
 * @author rmacario
 */
@Component
class CustomerDataTranslator {

    /** Converte um {@link CustomerCreateRequest} em {@link CustomerCreateSolicitation}. */
    CustomerCreateSolicitation toCustomerCreateSolicitation(
            @NonNull final CustomerCreateRequest customerCreateRequest) {
        return CustomerCreateSolicitation.builder()
                .name(customerCreateRequest.getName())
                .accountNumber(customerCreateRequest.getAccountNumber())
                .balance(customerCreateRequest.getAccountBalance())
                .build();
    }

    /** Converte um {@link Customer} em {@link CustomerCreateResponse}. */
    CustomerCreateResponse toCustomerResponse(@NonNull final Customer customer) {
        return CustomerCreateResponse.builder()
                .name(customer.getName())
                .createdAt(customer.getCreatedAt())
                .account(
                        CustomerCreateResponse.AccountResponse.builder()
                                .number(customer.getAccount().getNumber())
                                .balance(customer.getAccount().getBalance())
                                .build())
                .build();
    }
}
