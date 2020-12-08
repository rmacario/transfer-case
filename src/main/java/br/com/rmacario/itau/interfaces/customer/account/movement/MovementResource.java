package br.com.rmacario.itau.interfaces.customer.account.movement;

import static br.com.rmacario.itau.infrastructure.http.ResourceMediaType.APPLICATION_VND_V1;
import static br.com.rmacario.itau.interfaces.customer.account.AccountResource.ACCOUNT_PATH;

import br.com.rmacario.itau.application.customer.movement.MovementApplicationService;
import br.com.rmacario.itau.application.customer.movement.TransferFundsSolicitation;
import javax.validation.constraints.PositiveOrZero;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Resource que fornece métodos para realização de operações envolvendo valores.
 *
 * @author rmacario
 */
@Validated
@RestController
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping(
        path = MovementResource.MOVEMENT_PATH,
        produces = APPLICATION_VND_V1,
        consumes = APPLICATION_VND_V1)
class MovementResource {

    private static final String DEFAULT_PAGE = "0";

    static final String MOVEMENT_PATH = ACCOUNT_PATH + "/movements";

    MovementApplicationService movementApplicationService;

    AccountMovementDataTranslator translator;

    @PostMapping
    ResponseEntity<TransferFundsResponse> transferFunds(
            @Validated @RequestBody TransferFundsRequest request) {
        final var solicitation =
                TransferFundsSolicitation.builder()
                        .accountOrigin(request.getAccountOrigin())
                        .accountTarget(request.getAccountTarget())
                        .amount(request.getAmount())
                        .build();
        movementApplicationService.requestTransferFunds(solicitation);

        // Por questões de simplicidade o header location não será incluído.
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(TransferFundsResponse.builder().success(Boolean.TRUE).build());
    }

    @GetMapping
    ResponseEntity<Page<AccountMovementResponse>> findByAccountNumber(
            @RequestParam(value = "accountNumber") final Long accountNumber,
            @RequestParam(value = "page", required = false, defaultValue = DEFAULT_PAGE)
                    @PositiveOrZero
                    final Integer page) {
        final var accountMovementsFound =
                movementApplicationService.findByAccountNumber(accountNumber, page);
        return ResponseEntity.ok(accountMovementsFound.map(translator::toAccountMovementResponse));
    }
}
