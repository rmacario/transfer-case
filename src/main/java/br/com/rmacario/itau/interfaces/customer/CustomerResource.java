package br.com.rmacario.itau.interfaces.customer;

import static br.com.rmacario.itau.infrastructure.http.ResourceMediaType.APPLICATION_VND_V1;
import static br.com.rmacario.itau.interfaces.customer.CustomerResource.CUSTOMER_PATH;
import static org.springframework.http.HttpStatus.CREATED;

import br.com.rmacario.itau.application.customer.CustomerApplicationService;
import br.com.rmacario.itau.domain.customer.Customer;
import javax.validation.constraints.PositiveOrZero;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Resource que fornece métodos para manipulação de um {@link Customer}.
 *
 * @author rmacario
 */
@Validated
@RestController
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping(path = CUSTOMER_PATH, produces = APPLICATION_VND_V1, consumes = APPLICATION_VND_V1)
class CustomerResource {

    private static final String DEFAULT_PAGE = "0";

    static final String CUSTOMER_PATH = "/customers";

    CustomerApplicationService customerApplicationService;

    CustomerDataTranslator customerDataTranslator;

    @PostMapping
    ResponseEntity<CustomerResponse> create(@RequestBody CustomerCreateRequest request) {
        final var customerCreateSolicitation =
                customerDataTranslator.toCustomerCreateSolicitation(request);
        final var customer = customerApplicationService.create(customerCreateSolicitation);
        final var response = customerDataTranslator.toCustomerResponse(customer);

        return ResponseEntity.status(CREATED).body(response);
    }

    @GetMapping
    ResponseEntity<Page<CustomerResponse>> findAll(
            @RequestParam(value = "page", required = false, defaultValue = DEFAULT_PAGE)
                    @PositiveOrZero
                    final Integer page) {
        final var customersFound = customerApplicationService.findAllCustomersByPage(page);
        return ResponseEntity.ok(customersFound.map(customerDataTranslator::toCustomerResponse));
    }
}
