package br.com.rmacario.itau.application.customer;

import static lombok.AccessLevel.PACKAGE;
import static lombok.AccessLevel.PRIVATE;

import br.com.rmacario.itau.domain.customer.Customer;
import br.com.rmacario.itau.domain.customer.CustomerRepository;
import br.com.rmacario.itau.domain.customer.account.Account;
import br.com.rmacario.itau.domain.customer.account.AccountRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Serviço que orquestra as chamadas aos casos de uso da aplicação que envolvem a manipulação de um
 * {@link Customer}.
 *
 * @author rmacario
 */
@Service
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor(access = PACKAGE, onConstructor_ = @Autowired)
public class CustomerApplicationService {

    CustomerRepository customerRepository;

    AccountRepository accountRepository;

    @Transactional
    public Customer create(@NonNull final CustomerCreateSolicitation customerData) {
        final var optionalAccount = accountRepository.findByNumber(customerData.getAccountNumber());

        if (optionalAccount.isPresent()) {
            throw new AccountNumberAlreadyExistsException(customerData.getAccountNumber());
        }

        final var customer =
                Customer.builder()
                        .name(customerData.getName())
                        .account(
                                Account.builder()
                                        .number(customerData.getAccountNumber())
                                        .balance(customerData.getBalance())
                                        .build())
                        .build()
                        .bindAccount();

        return customerRepository.save(customer);
    }
}
