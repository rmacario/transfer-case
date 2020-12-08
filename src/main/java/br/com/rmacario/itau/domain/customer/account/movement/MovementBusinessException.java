package br.com.rmacario.itau.domain.customer.account.movement;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;

/**
 * Exceção genérica para facilitar captura de problemas ocorridos em decorrência de regras de
 * negócio não satisfeitas durante uma movimentação de fundos.
 *
 * @author rmacario
 */
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MovementBusinessException extends RuntimeException {

    MovementErrorType errorType;

    public MovementBusinessException(
            @NonNull final MovementErrorType errorType, @NonNull final String errorMessage) {
        super(errorMessage);
        this.errorType = errorType;
    }
}
