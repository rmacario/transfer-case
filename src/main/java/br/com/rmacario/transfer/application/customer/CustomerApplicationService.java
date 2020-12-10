package br.com.rmacario.transfer.application.customer;

import static lombok.AccessLevel.PRIVATE;

import br.com.rmacario.transfer.domain.customer.Customer;
import br.com.rmacario.transfer.domain.customer.CustomerRepository;
import br.com.rmacario.transfer.domain.customer.account.Account;
import br.com.rmacario.transfer.domain.customer.account.AccountRepository;
import br.com.rmacario.transfer.interfaces.customer.CustomerResource;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
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

    private static final Logger LOGGER = LoggerFactory.getLogger(CustomerResource.class);

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
            LOGGER.error(
                    "msg=Account number [{}] already exists.", customerData.getAccountNumber());
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
        return customerRepository.findAll(PageRequest.of(page, defaultPageSize, Sort.by("id")));
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
