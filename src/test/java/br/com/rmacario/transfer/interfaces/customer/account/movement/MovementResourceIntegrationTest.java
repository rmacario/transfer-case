package br.com.rmacario.transfer.interfaces.customer.account.movement;

import static br.com.rmacario.transfer.domain.customer.account.movement.MovementErrorType.INSUFFICIENT_FUNDS;
import static br.com.rmacario.transfer.domain.customer.account.movement.MovementErrorType.SAME_ORIGIN;
import static br.com.rmacario.transfer.domain.customer.account.movement.MovementErrorType.TRANSFER_LIMIT_EXCEEDED;
import static br.com.rmacario.transfer.domain.customer.account.movement.MovementErrorType.valueOf;
import static br.com.rmacario.transfer.infrastructure.http.ResourceMediaType.APPLICATION_VND_V1;
import static br.com.rmacario.transfer.interfaces.customer.account.movement.MovementResource.MOVEMENT_PATH;
import static java.math.BigDecimal.ONE;
import static java.math.BigDecimal.TEN;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.params.provider.Arguments.of;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpHeaders.ACCEPT;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NOT_FOUND;

import br.com.rmacario.transfer.application.customer.CustomerApplicationService;
import br.com.rmacario.transfer.application.customer.CustomerCreateSolicitation;
import br.com.rmacario.transfer.domain.customer.account.AccountRepository;
import br.com.rmacario.transfer.domain.customer.account.movement.AccountMovementRepository;
import br.com.rmacario.transfer.domain.customer.account.movement.MovementErrorType;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.util.stream.Stream;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@FieldDefaults(level = AccessLevel.PRIVATE)
@SpringBootTest(webEnvironment = RANDOM_PORT)
class MovementResourceIntegrationTest {

    private static final String HOST = "http://localhost:";

    private static final Long ORIGIN_ACCOUNT_NUMBER = 10l;

    private static final Long TARGET_ACCOUNT_NUMBER = 20l;

    private static final Long THIRD_ACCOUNT_NUMBER = 30l;

    private static final Long NONEXISTENT_ACCOUNT_NUMBER = 100l;

    private static final String NAME = "name";

    private static final BigDecimal BALANCE = TEN;

    Integer appPort;

    TestRestTemplate restTemplate;

    CustomerApplicationService customerApplicationService;

    AccountRepository accountRepository;

    AccountMovementRepository accountMovementRepository;

    BigDecimal transferLimitAmount;

    String resourcePath;

    HttpHeaders headers;

    @Autowired
    MovementResourceIntegrationTest(
            @LocalServerPort final Integer appPort,
            final TestRestTemplate restTemplate,
            final CustomerApplicationService customerApplicationService,
            final AccountRepository accountRepository,
            final AccountMovementRepository accountMovementRepository,
            @Value("${app.domain.movements.transfer-limit-amount}")
                    final BigDecimal transferLimitAmount) {
        this.appPort = appPort;
        this.restTemplate = restTemplate;
        this.customerApplicationService = customerApplicationService;
        this.accountRepository = accountRepository;
        this.accountMovementRepository = accountMovementRepository;
        this.transferLimitAmount = transferLimitAmount;
        this.resourcePath = HOST + appPort + "/api" + MOVEMENT_PATH;
        this.headers = new HttpHeaders();
        this.headers.set(ACCEPT, APPLICATION_VND_V1);
        this.headers.set(CONTENT_TYPE, APPLICATION_VND_V1);
    }

    @BeforeEach
    void setup() {
        accountMovementRepository.deleteAll();

        if (!accountRepository.findByNumber(ORIGIN_ACCOUNT_NUMBER).isPresent()) {
            saveNewCustomer(ORIGIN_ACCOUNT_NUMBER, BALANCE);
        }

        if (!accountRepository.findByNumber(TARGET_ACCOUNT_NUMBER).isPresent()) {
            saveNewCustomer(TARGET_ACCOUNT_NUMBER, BALANCE);
        }

        if (!accountRepository.findByNumber(THIRD_ACCOUNT_NUMBER).isPresent()) {
            saveNewCustomer(THIRD_ACCOUNT_NUMBER, BALANCE);
        }
    }

    @ParameterizedTest
    @MethodSource("getParams")
    void transferFunds_parametrized_shouldReturnStatusAsExpected(
            final HttpStatus httpStatus,
            final Long accountOrigin,
            final Long accountTarget,
            final MovementErrorType errorType)
            throws JsonProcessingException {
        final var om = new ObjectMapper();

        if (NOT_FOUND == httpStatus) {
            final var response = doRequest(accountOrigin, accountTarget, TEN);
            assertEquals(NOT_FOUND, response.getStatusCode());

        } else {
            if (BAD_REQUEST == httpStatus) {
                ResponseEntity<String> response = null;
                switch (errorType) {
                    case INSUFFICIENT_FUNDS:
                        response = doRequest(accountOrigin, accountTarget, BALANCE.add(ONE));
                        break;
                    case SAME_ORIGIN:
                        response = doRequest(accountOrigin, accountTarget, BALANCE);
                        break;
                    case TRANSFER_LIMIT_EXCEEDED:
                        response =
                                doRequest(
                                        accountOrigin, accountTarget, transferLimitAmount.add(ONE));
                        break;
                }
                final var returnedErrorType =
                        valueOf(om.readTree(response.getBody()).get("error").get("type").asText());
                assertEquals(errorType, returnedErrorType);
                assertEquals(1, accountMovementRepository.findAll().size());

            } else {
                final var response = doRequest(accountOrigin, accountTarget, ONE);
                assertEquals(httpStatus, response.getStatusCode());
                assertEquals(2, accountMovementRepository.findAll().size());
            }
        }
    }

    // ----------------------------------
    // Privates

    private void saveNewCustomer(final Long originAccountNumber, final BigDecimal balance) {
        final var customerOne =
                CustomerCreateSolicitation.builder()
                        .accountNumber(originAccountNumber)
                        .name(NAME)
                        .balance(balance)
                        .build();
        customerApplicationService.create(customerOne);
    }

    private ResponseEntity<String> doRequest(
            final Long accountOrigin, final Long accountTarget, final BigDecimal transferValue) {
        final var request =
                TransferFundsRequest.builder()
                        .accountOrigin(accountOrigin)
                        .accountTarget(accountTarget)
                        .amount(transferValue)
                        .build();
        final var requestEntity = new HttpEntity<>(request, headers);
        return restTemplate.exchange(resourcePath, POST, requestEntity, String.class);
    }

    static Stream<Arguments> getParams() {
        return Stream.of(
                of(NOT_FOUND, NONEXISTENT_ACCOUNT_NUMBER, TARGET_ACCOUNT_NUMBER, null),
                of(NOT_FOUND, ORIGIN_ACCOUNT_NUMBER, NONEXISTENT_ACCOUNT_NUMBER, null),
                of(BAD_REQUEST, ORIGIN_ACCOUNT_NUMBER, TARGET_ACCOUNT_NUMBER, INSUFFICIENT_FUNDS),
                of(BAD_REQUEST, ORIGIN_ACCOUNT_NUMBER, ORIGIN_ACCOUNT_NUMBER, SAME_ORIGIN),
                of(
                        BAD_REQUEST,
                        THIRD_ACCOUNT_NUMBER,
                        TARGET_ACCOUNT_NUMBER,
                        TRANSFER_LIMIT_EXCEEDED),
                of(CREATED, ORIGIN_ACCOUNT_NUMBER, TARGET_ACCOUNT_NUMBER, null));
    }
}
