package br.com.rmacario.transfer.interfaces.customer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import br.com.rmacario.transfer.application.customer.CustomerApplicationService;
import br.com.rmacario.transfer.application.customer.CustomerCreateSolicitation;
import br.com.rmacario.transfer.domain.customer.Customer;
import java.util.List;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;

@ExtendWith(MockitoExtension.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
class CustomerResourceTest {

    CustomerResource customerResource;

    @Mock CustomerApplicationService customerApplicationService;

    @Mock CustomerDataTranslator customerDataTranslator;

    @Mock CustomerCreateRequest request;

    @Mock CustomerCreateSolicitation customerCreateSolicitation;

    @Mock CustomerResponse response;

    @Mock Customer customer;

    @BeforeEach
    void setup() {
        this.customerResource =
                new CustomerResource(customerApplicationService, customerDataTranslator);
    }

    @Test
    void create_customerCreatedSuccessfully_shouldReturnHttpStatusCreatedWithBody() {
        when(customerDataTranslator.toCustomerCreateSolicitation(request))
                .thenReturn(customerCreateSolicitation);
        when(customerApplicationService.create(customerCreateSolicitation)).thenReturn(customer);
        when(customerDataTranslator.toCustomerResponse(customer)).thenReturn(response);

        final var creationResponse = customerResource.create(request);

        assertEquals(HttpStatus.CREATED, creationResponse.getStatusCode());
        assertEquals(response, creationResponse.getBody());
    }

    @Test
    void findAll_twiceCustomersFound_shouldReturnPagedCustomers() {
        final var page = 1;
        final var pagedCustomers = new PageImpl<>(List.of(customer, customer));
        when(customerApplicationService.findAllCustomersByPage(page)).thenReturn(pagedCustomers);
        when(customerDataTranslator.toCustomerResponse(any())).thenReturn(response, response);

        final var responseEntity = customerResource.findAll(page);

        verify(customerDataTranslator, times(pagedCustomers.getNumberOfElements()))
                .toCustomerResponse(any());
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(
                pagedCustomers.getNumberOfElements(), responseEntity.getBody().getTotalElements());
    }
}
