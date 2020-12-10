package br.com.rmacario.transfer.application.customer;

import static lombok.AccessLevel.PRIVATE;

import java.math.BigDecimal;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

/**
 * Dados necessários para registrar um novo cliente.
 *
 * @author rmacario
 */
@Getter
@Builder
@ToString
@EqualsAndHashCode
@FieldDefaults(level = PRIVATE)
@AllArgsConstructor(access = PRIVATE)
public class CustomerCreateSolicitation {

    /** Nome do cliente. */
    @NonNull String name;

    /** Número da conta. */
    @NotNull Long accountNumber;

    /** Saldo inicial utilizado na criação da conta. */
    @NotNull BigDecimal balance;
}
