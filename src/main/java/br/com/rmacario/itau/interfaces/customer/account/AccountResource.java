package br.com.rmacario.itau.interfaces.customer.account;

import static br.com.rmacario.itau.infrastructure.http.ResourceMediaType.APPLICATION_VND_V1;
import static br.com.rmacario.itau.interfaces.customer.CustomerResource.CUSTOMER_PATH;
import static br.com.rmacario.itau.interfaces.customer.account.AccountResource.ACCOUNT_PATH;

import br.com.rmacario.itau.application.customer.CustomerApplicationService;
import br.com.rmacario.itau.domain.customer.account.Account;
import br.com.rmacario.itau.interfaces.customer.CustomerDataTranslator;
import br.com.rmacario.itau.interfaces.customer.CustomerResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Resource que fornece métodos para manipulação de uma {@link Account}.
 *
 * @author rmacario
 */
@Validated
@RestController
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping(path = ACCOUNT_PATH, produces = APPLICATION_VND_V1, consumes = APPLICATION_VND_V1)
public class AccountResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(AccountResource.class);

    public static final String ACCOUNT_PATH = CUSTOMER_PATH + "/accounts";

    CustomerApplicationService customerApplicationService;

    CustomerDataTranslator customerDataTranslator;

    @GetMapping
    ResponseEntity<CustomerResponse> findByAccountNumber(
            @RequestParam(value = "number", required = false) final Long accountNumber) {
        LOGGER.info("accountNumber={}.", accountNumber);
        final var customerFound = customerApplicationService.findByAccountNumber(accountNumber);
        return ResponseEntity.ok(customerDataTranslator.toCustomerResponse(customerFound));
    }
}
