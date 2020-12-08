package br.com.rmacario.itau.interfaces.customer.account.movement;

import java.math.BigDecimal;
import java.util.Objects;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

/**
 * Aplica as regras de validação de range de valores sobre o campo anotado com {@link LimitAmount}.
 *
 * @author rmacario
 * @see LimitAmount
 */
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LimitAmountValidator implements ConstraintValidator<LimitAmount, BigDecimal> {

    BigDecimal limitAmount;

    @Override
    public void initialize(final LimitAmount limitAmount) {
        this.limitAmount = new BigDecimal(limitAmount.value());
    }

    @Override
    public boolean isValid(final BigDecimal value, final ConstraintValidatorContext context) {
        if (Objects.isNull(value)) {
            return true;
        }

        return limitAmount.compareTo(value) >= 0;
    }
}
