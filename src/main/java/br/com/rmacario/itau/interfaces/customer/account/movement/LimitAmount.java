package br.com.rmacario.itau.interfaces.customer.account.movement;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.validation.Constraint;
import javax.validation.Payload;

/**
 * Valida se o valor informado está dentro do limite configurado.
 *
 * @author rmacario
 */
@Documented
@Constraint(validatedBy = LimitAmountValidator.class)
@Target({ElementType.TYPE, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface LimitAmount {

    String message() default "valor máximo permitido é de {value}.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    String value();
}
