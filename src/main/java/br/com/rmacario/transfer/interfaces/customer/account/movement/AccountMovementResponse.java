package br.com.rmacario.transfer.interfaces.customer.account.movement;

import br.com.rmacario.transfer.domain.customer.account.movement.MovementType;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

/**
 * Payload de resposta de uma busca por movimentações financeiras de uma conta.
 *
 * @author rmacario
 */
@Getter
@Builder
@ToString
@EqualsAndHashCode
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AccountMovementResponse {

    @NonNull BigDecimal value;

    @NonNull ZonedDateTime createdAt;

    @NonNull MovementType type;

    @NonNull Long accountOrigin;

    @NonNull Long accountTarget;

    @NonNull Boolean success;
}
