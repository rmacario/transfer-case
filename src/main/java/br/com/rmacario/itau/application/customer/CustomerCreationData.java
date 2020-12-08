package br.com.rmacario.itau.application.customer;

import static lombok.AccessLevel.PRIVATE;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

/**
 * Informações de resposta do cadastro do cliente.
 *
 * @author rmacario
 */
@Getter
@Builder
@ToString
@EqualsAndHashCode
@FieldDefaults(level = PRIVATE)
@AllArgsConstructor(access = PRIVATE)
public class CustomerCreationData {

    /** Id atribuído ao cadastro do cliente. */
    @NonNull Long id;

    /** Nome do cliente. */
    @NonNull String name;

    /** Id atribuído à conta do cliente. */
    @NonNull Long accountId;

    /** Número da conta. */
    @NonNull Long accountNumber;

    /** Saldo total em conta. */
    @NonNull BigDecimal accountBalance;

    /** Data de criação do cadastro. */
    @NonNull ZonedDateTime createdAt;
}
