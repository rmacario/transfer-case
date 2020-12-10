package br.com.rmacario.transfer.interfaces.customer;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

/**
 * Payload de resposta com os dados do cliente.
 *
 * @author rmacario
 */
@Getter
@Builder
@ToString
@EqualsAndHashCode
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CustomerResponse {

    /** Nome do cliente. */
    String name;

    /** Data de criação do cadastro. */
    ZonedDateTime createdAt;

    /** Dados da conta. */
    AccountResponse account;

    /** Payload de resposta com os dados da conta do cliente. */
    @Getter
    @Builder
    @ToString
    @EqualsAndHashCode
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
    public static class AccountResponse {

        /** Número da conta do cliente. */
        Long number;

        /** Saldo total. */
        BigDecimal balance;
    }
}
