package br.com.rmacario.transfer.interfaces.customer.account.movement;

import br.com.rmacario.transfer.domain.customer.account.movement.MovementErrorType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

/**
 * Payload de resposta para um pedido de transferência de fundos.
 *
 * @author rmacario
 */
@Getter
@Builder
@ToString
@EqualsAndHashCode
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TransferFundsResponse {

    @NonNull Boolean success;

    Error error;

    @Getter
    @Builder
    @ToString
    @EqualsAndHashCode
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
    public static class Error {

        @NonNull String message;

        @NonNull MovementErrorType type;
    }
}
