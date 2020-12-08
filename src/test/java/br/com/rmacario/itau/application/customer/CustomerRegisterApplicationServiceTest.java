package br.com.rmacario.itau.application.customer;

import static lombok.AccessLevel.PRIVATE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import br.com.rmacario.itau.domain.customer.Customer;
import br.com.rmacario.itau.domain.customer.CustomerRepository;
import br.com.rmacario.itau.domain.customer.account.Account;
import br.com.rmacario.itau.domain.customer.account.AccountRepository;
import java.math.BigDecimal;
import java.util.Optional;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@FieldDefaults(level = PRIVATE)
@ExtendWith(MockitoExtension.class)
class CustomerApplicationServiceTest {

    private static final String NAME = "name";

    private static final Long ACCOUNT_NUMBER = 10l;

    private static final BigDecimal BALANCE = new BigDecimal(250l);

    CustomerApplicationService customerApplicationService;

    @Mock CustomerRepository customerRepository;

    @Mock AccountRepository accountRepository;

    @Mock Account account;

    @Mock CustomerCreateSolicitation customerCreateSolicitation;

    @Mock Customer customer;

    @BeforeEach
    void setup() {
        this.customerApplicationService =
                new CustomerApplicationService(customerRepository, accountRepository);
    }

    @Test
    void create_parameterNull_shouldThrowIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> customerApplicationService.create(null));
    }

    @Test
    void create_accountAlreadyExists_AccountNumberAlreadyExistsException() {
        when(customerCreateSolicitation.getAccountNumber()).thenReturn(ACCOUNT_NUMBER);
        when(accountRepository.findByNumber(any())).thenReturn(Optional.of(account));
        assertThrows(
                AccountNumberAlreadyExistsException.class,
                () -> customerApplicationService.create(customerCreateSolicitation));
    }

    @Test
    void create_parametersOk_shouldCreateNewCustomer() {
        when(customerCreateSolicitation.getAccountNumber()).thenReturn(ACCOUNT_NUMBER);
        when(accountRepository.findByNumber(any())).thenReturn(Optional.empty());
        when(customerCreateSolicitation.getName()).thenReturn(NAME);
        when(customerCreateSolicitation.getBalance()).thenReturn(BALANCE);
        when(customerRepository.save(any())).thenReturn(customer);

        final var persistedCustomer = customerApplicationService.create(customerCreateSolicitation);

        final var customerArgumentCaptor = ArgumentCaptor.forClass(Customer.class);
        verify(customerRepository).save(customerArgumentCaptor.capture());

        final var capturedCustomer = customerArgumentCaptor.getValue();
        assertNotNull(capturedCustomer);
        assertNotNull(capturedCustomer.getCreatedAt());
        assertEquals(NAME, capturedCustomer.getName());
        assertEquals(ACCOUNT_NUMBER, capturedCustomer.getAccount().getNumber());
        assertEquals(BALANCE, capturedCustomer.getAccount().getBalance());
        assertEquals(capturedCustomer, capturedCustomer.getAccount().getCustomer());
        assertEquals(persistedCustomer, persistedCustomer);
    }
}
