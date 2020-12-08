package br.com.rmacario.itau.interfaces.customer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import br.com.rmacario.itau.domain.customer.Customer;
import br.com.rmacario.itau.domain.customer.account.Account;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
class CustomerDataTranslatorTest {

    private static final String NAME = "name";

    private static final Long ACCOUNT_NUMBER = 10l;

    private static final BigDecimal ACCOUNT_BALANCE = BigDecimal.TEN;

    private static final ZonedDateTime CREATED_AT = ZonedDateTime.now();

    CustomerDataTranslator customerDataTranslator;

    @Mock CustomerCreateRequest request;

    @Mock Customer customer;

    @Mock Account account;

    @BeforeEach
    void setup() {
        this.customerDataTranslator = new CustomerDataTranslator();
    }

    @Test
    void toCustomerCreateSolicitation_parameterNull_shouldThrowIllegalArgumentException() {
        assertThrows(
                IllegalArgumentException.class,
                () -> customerDataTranslator.toCustomerCreateSolicitation(null));
    }

    @Test
    void toCustomerCreateSolicitation_parameterOk_shouldReturnCustomerCreateSolicitation() {
        when(request.getName()).thenReturn(NAME);
        when(request.getAccountNumber()).thenReturn(ACCOUNT_NUMBER);
        when(request.getAccountBalance()).thenReturn(ACCOUNT_BALANCE);

        final var customerCreateSolicitation =
                customerDataTranslator.toCustomerCreateSolicitation(request);

        assertNotNull(customerCreateSolicitation);
        assertEquals(NAME, customerCreateSolicitation.getName());
        assertEquals(ACCOUNT_NUMBER, customerCreateSolicitation.getAccountNumber());
        assertEquals(ACCOUNT_BALANCE, customerCreateSolicitation.getBalance());
    }

    @Test
    void toCustomerResponse_parameterNull_shouldThrowIllegalArgumentException() {
        assertThrows(
                IllegalArgumentException.class,
                () -> customerDataTranslator.toCustomerResponse(null));
    }

    @Test
    void toCustomerResponse_parameterOk_shouldReturnCustomerResponse() {
        when(customer.getName()).thenReturn(NAME);
        when(customer.getCreatedAt()).thenReturn(CREATED_AT);
        when(customer.getAccount()).thenReturn(account);
        when(account.getNumber()).thenReturn(ACCOUNT_NUMBER);
        when(account.getBalance()).thenReturn(ACCOUNT_BALANCE);

        final var customerCreateResponse = customerDataTranslator.toCustomerResponse(customer);

        assertNotNull(customerCreateResponse);
        assertEquals(NAME, customerCreateResponse.getName());
        assertEquals(CREATED_AT, customerCreateResponse.getCreatedAt());
        assertEquals(ACCOUNT_NUMBER, customerCreateResponse.getAccount().getNumber());
        assertEquals(ACCOUNT_BALANCE, customerCreateResponse.getAccount().getBalance());
    }
}
