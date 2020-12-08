package br.com.rmacario.itau.interfaces.customer;

import static lombok.AccessLevel.PRIVATE;

import java.math.BigDecimal;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

/**
 * Payload de requisição de criação de um cliente.
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
class CustomerCreateRequest {

    /** Nome do cliente. */
    @NotEmpty String name;

    /** Número da conta. */
    @NotNull Long accountNumber;

    /** Saldo inicial utilizado na criação da conta. */
    @NotNull BigDecimal accountBalance;
}
