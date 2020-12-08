package br.com.rmacario.itau.interfaces.customer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import br.com.rmacario.itau.application.customer.CustomerCreateSolicitation;
import br.com.rmacario.itau.application.customer.CustomerCreationData;
import br.com.rmacario.itau.application.customer.CustomerRegisterApplicationService;
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
class CustomerResourceTest {

    CustomerResource customerResource;

    @Mock CustomerRegisterApplicationService customerRegisterApplicationService;

    @Mock CustomerDataTranslator customerDataTranslator;

    @Mock CustomerCreateRequest request;

    @Mock CustomerCreateSolicitation customerCreateSolicitation;

    @Mock CustomerCreationData customerCreationData;

    @Mock CustomerCreateResponse response;

    @BeforeEach
    void setup() {
        this.customerResource =
                new CustomerResource(customerRegisterApplicationService, customerDataTranslator);
    }

    @Test
    void create_customerCreatedSuccessfully_shouldReturnHttpStatusCreatedWithBody() {
        when(customerDataTranslator.toCustomerCreateSolicitation(request))
                .thenReturn(customerCreateSolicitation);
        when(customerRegisterApplicationService.create(customerCreateSolicitation))
                .thenReturn(customerCreationData);
        when(customerDataTranslator.toCustomerResponse(customerCreationData)).thenReturn(response);

        final var creationResponse = customerResource.create(request);

        assertEquals(HttpStatus.CREATED, creationResponse.getStatusCode());
        assertEquals(response, creationResponse.getBody());
    }
}
