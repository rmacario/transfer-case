package br.com.rmacario.transfer.interfaces.customer.account.movement;

import static lombok.AccessLevel.PRIVATE;

import java.math.BigDecimal;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

/**
 * Payload de requisição de transferência de fundos entre contas.
 *
 * @author rmacario
 */
@Getter
@Builder
@ToString
@EqualsAndHashCode
@FieldDefaults(level = PRIVATE)
@NoArgsConstructor(access = PRIVATE)
@AllArgsConstructor(access = PRIVATE)
class TransferFundsRequest {

    @NotNull Long accountOrigin;

    @NotNull Long accountTarget;

    @PositiveOrZero BigDecimal amount;
}
