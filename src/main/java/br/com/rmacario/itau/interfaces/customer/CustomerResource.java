package br.com.rmacario.itau.interfaces.customer;

import static br.com.rmacario.itau.infrastructure.http.ResourceMediaType.APPLICATION_VND_V1;
import static br.com.rmacario.itau.interfaces.customer.CustomerResource.CUSTOMER_PATH;
import static org.springframework.http.HttpStatus.CREATED;

import br.com.rmacario.itau.application.customer.CustomerRegisterApplicationService;
import br.com.rmacario.itau.domain.customer.Customer;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Resource que fornece métodos para manipulação de um {@link Customer}.
 *
 * @author rmacario
 */
@RestController
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping(path = CUSTOMER_PATH, produces = APPLICATION_VND_V1, consumes = APPLICATION_VND_V1)
class CustomerResource {

    static final String CUSTOMER_PATH = "/customers";

    CustomerRegisterApplicationService customerRegisterApplicationService;

    CustomerDataTranslator customerDataTranslator;

    @PostMapping
    ResponseEntity<CustomerCreateResponse> create(@Validated CustomerCreateRequest request) {
        final var customerCreateSolicitation =
                customerDataTranslator.toCustomerCreateSolicitation(request);
        final var customerCreateData =
                customerRegisterApplicationService.create(customerCreateSolicitation);
        final var response = customerDataTranslator.toCustomerResponse(customerCreateData);

        return ResponseEntity.status(CREATED).body(response);
    }
}
