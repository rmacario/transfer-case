package br.com.rmacario.transfer.interfaces.customer.account.movement;

import br.com.rmacario.transfer.domain.customer.account.movement.AccountMovement;
import lombok.NonNull;
import org.springframework.stereotype.Component;

/**
 * Classe que contém a lógica de conversão de objetos de resposta da API em {@link AccountMovement}.
 *
 * @author rmacario
 */
@Component
class AccountMovementDataTranslator {

    AccountMovementResponse toAccountMovementResponse(
            @NonNull final AccountMovement accountMovement) {
        return AccountMovementResponse.builder()
                .value(accountMovement.getValue())
                .accountOrigin(accountMovement.getAccountOrigin().getNumber())
                .accountTarget(accountMovement.getAccountTarget().getNumber())
                .createdAt(accountMovement.getCreatedAt())
                .type(accountMovement.getType())
                .success(accountMovement.getSuccess())
                .build();
    }
}
