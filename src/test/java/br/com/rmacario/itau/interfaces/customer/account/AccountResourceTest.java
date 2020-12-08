package br.com.rmacario.itau.interfaces.customer.account;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

import br.com.rmacario.itau.application.customer.CustomerApplicationService;
import br.com.rmacario.itau.domain.customer.Customer;
import br.com.rmacario.itau.interfaces.customer.CustomerDataTranslator;
import br.com.rmacario.itau.interfaces.customer.CustomerResponse;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

@ExtendWith(MockitoExtension.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
class AccountResourceTest {

    private static final long ACCOUNT_NUMBER = 10L;

    AccountResource accountResource;

    @Mock CustomerApplicationService customerApplicationService;

    @Mock CustomerDataTranslator customerDataTranslator;

    @Mock Customer customer;

    @Mock CustomerResponse customerResponse;

    @BeforeEach
    void setup() {
        this.accountResource =
                new AccountResource(customerApplicationService, customerDataTranslator);
    }

    @Test
    void findByAccountNumber_customerFound_shouldReturnCustomerFound() {
        when(customerApplicationService.findByAccountNumber(ACCOUNT_NUMBER)).thenReturn(customer);
        when(customerDataTranslator.toCustomerResponse(customer)).thenReturn(customerResponse);

        final var customerFound = accountResource.findByAccountNumber(ACCOUNT_NUMBER);
        assertNotNull(customerFound);
        assertEquals(HttpStatus.OK, customerFound.getStatusCode());
        assertEquals(customerResponse, customerFound.getBody());
    }
}
