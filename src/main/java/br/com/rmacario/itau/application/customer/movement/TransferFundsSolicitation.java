package br.com.rmacario.itau.application.customer.movement;

import static lombok.AccessLevel.PRIVATE;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

@Getter
@Builder
@ToString
@EqualsAndHashCode
@FieldDefaults(level = PRIVATE)
@AllArgsConstructor(access = PRIVATE)
public class TransferFundsSolicitation {

    @NonNull Long accountOrigin;

    @NonNull Long accountTarget;

    @NonNull BigDecimal amount;
}
