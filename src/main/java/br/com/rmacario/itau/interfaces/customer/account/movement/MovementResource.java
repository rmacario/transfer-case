package br.com.rmacario.itau.interfaces.customer.account.movement;

import static br.com.rmacario.itau.infrastructure.http.ResourceMediaType.APPLICATION_VND_V1;
import static br.com.rmacario.itau.interfaces.customer.account.AccountResource.ACCOUNT_PATH;

import br.com.rmacario.itau.application.customer.movement.MovementApplicationService;
import br.com.rmacario.itau.application.customer.movement.TransferFundsSolicitation;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Resource que fornece métodos para realização de operações envolvendo valores.
 *
 * @author rmacario
 */
@RestController
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping(
        path = MovementResource.MOVEMENT_PATH,
        produces = APPLICATION_VND_V1,
        consumes = APPLICATION_VND_V1)
class MovementResource {

    static final String MOVEMENT_PATH = ACCOUNT_PATH + "/movements";

    MovementApplicationService movementApplicationService;

    @PostMapping
    ResponseEntity<Void> transferFunds(@Validated @RequestBody TransferFundsRequest request) {
        final var solicitation =
                TransferFundsSolicitation.builder()
                        .accountOrigin(request.getAccountOrigin())
                        .accountTarget(request.getAccountTarget())
                        .amount(request.getAmount())
                        .build();
        movementApplicationService.requestTransferFunds(solicitation);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
