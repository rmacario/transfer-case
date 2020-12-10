package br.com.rmacario.transfer.interfaces.customer;

import static br.com.rmacario.transfer.infrastructure.http.ResourceMediaType.APPLICATION_VND_V1;
import static br.com.rmacario.transfer.interfaces.customer.CustomerResource.CUSTOMER_PATH;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpHeaders.ACCEPT;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.CREATED;

import java.math.BigDecimal;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;

@FieldDefaults(level = AccessLevel.PRIVATE)
@SpringBootTest(webEnvironment = RANDOM_PORT)
class CustomerResourceIntegrationTest {

    private static final String HOST = "http://localhost:";

    private static final Long ACCOUNT_NUMBER = 10l;

    private static final BigDecimal BALANCE = BigDecimal.TEN;

    private static final String NAME = "name";

    Integer appPort;

    TestRestTemplate restTemplate;

    String resourcePath;

    HttpHeaders headers;

    @Autowired
    CustomerResourceIntegrationTest(
            @LocalServerPort final Integer appPort, final TestRestTemplate restTemplate) {
        this.appPort = appPort;
        this.restTemplate = restTemplate;
        this.resourcePath = HOST + appPort + "/api" + CUSTOMER_PATH;
        this.headers = new HttpHeaders();
        this.headers.set(ACCEPT, APPLICATION_VND_V1);
        this.headers.set(CONTENT_TYPE, APPLICATION_VND_V1);
    }

    @Test
    void create_twiceCalls_shouldCreateOnFirstCallAndReturnConflictStatusCodeOnSecond() {
        final var request =
                CustomerCreateRequest.builder()
                        .accountBalance(BALANCE)
                        .accountNumber(ACCOUNT_NUMBER)
                        .name(NAME)
                        .build();
        final var requestEntity = new HttpEntity<>(request, headers);
        final var firstResponse =
                restTemplate.exchange(resourcePath, POST, requestEntity, CustomerResponse.class);
        assertEquals(CREATED, firstResponse.getStatusCode());

        final var secondResponse =
                restTemplate.exchange(resourcePath, POST, requestEntity, String.class);
        assertEquals(CONFLICT, secondResponse.getStatusCode());
    }
}
