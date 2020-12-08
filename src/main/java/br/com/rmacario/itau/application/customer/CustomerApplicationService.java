package br.com.rmacario.itau.application.customer;

import static lombok.AccessLevel.PRIVATE;

import br.com.rmacario.itau.domain.customer.Customer;
import br.com.rmacario.itau.domain.customer.CustomerRepository;
import br.com.rmacario.itau.domain.customer.account.Account;
import br.com.rmacario.itau.domain.customer.account.AccountRepository;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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
public class CustomerApplicationService {

    CustomerRepository customerRepository;

    AccountRepository accountRepository;

    Integer defaultPageSize;

    @Autowired
    CustomerApplicationService(
            final CustomerRepository customerRepository,
            final AccountRepository accountRepository,
            @Value("${app.repository.pagination.default-size}") final Integer defaultPageSize) {
        this.customerRepository = customerRepository;
        this.accountRepository = accountRepository;
        this.defaultPageSize = defaultPageSize;
    }

    /**
     * Realiza o processo de criação de um novo {@link Customer}, incluindo também a {@link Account}
     * relacionada a ele.
     *
     * @param customerData Informações utilizadas na criação do customer.
     * @return Customer cadastrado.
     */
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

    /**
     * Realiza uma busca paginada, sem filtros e ordenada por ID, pelos {@link Customer}
     * cadastrados.
     *
     * @param page Informações sobre a pagina utilizada na pesquisa.
     * @return {@link Page} de customers.
     */
    public Page<Customer> findAllCustomersByPage(final int page) {
        return customerRepository.findByOrderByIdAsc(PageRequest.of(page, defaultPageSize));
    }

    /**
     * Realiza a busca de um {@link Customer} pelo número de sua conta.
     *
     * @param accountNumber Número da conta utilizado na pesquisa.
     * @return {@link Customer} encontrado.
     */
    public Customer findByAccountNumber(final long accountNumber) {
        return customerRepository.findOneByAccountNumber(accountNumber);
    }
}
