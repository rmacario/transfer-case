package br.com.rmacario.itau.interfaces;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_EMPTY;
import static lombok.AccessLevel.PRIVATE;

import br.com.rmacario.itau.application.customer.AccountNumberAlreadyExistsException;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import javax.persistence.EntityNotFoundException;
import javax.validation.ConstraintViolationException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Singular;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import org.hibernate.validator.internal.engine.path.PathImpl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 * Configura o tratamento de exceções lançadas durante o processamento de requisições HTTP.
 *
 * @author rmacario
 */
@ControllerAdvice
class ResourceExceptionHandler extends ResponseEntityExceptionHandler {

    private static final String BEAN_VALIDATIONS_MSG = "Campo(s) não preenchido(s) corretamente.";

    /** Trata exceções lançadas por erros de Beans Validations no body de requisições.. */
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            final MethodArgumentNotValidException ex,
            final HttpHeaders headers,
            final HttpStatus status,
            final WebRequest request) {
        final BindingResult result = ex.getBindingResult();
        return ResponseEntity.badRequest().body(processErrors(result, BEAN_VALIDATIONS_MSG));
    }

    /** Trata exceções lançadas por erros de Beans Validations nos parâmetros do endpoint. */
    @ExceptionHandler(ConstraintViolationException.class)
    ResponseEntity<Object> handleConstraintViolationException(
            final ConstraintViolationException ex) {
        return ResponseEntity.badRequest().body(processErrors(ex, BEAN_VALIDATIONS_MSG));
    }

    @ExceptionHandler(AccountNumberAlreadyExistsException.class)
    ResponseEntity<Object> handleAccountNumberAlreadyExistsException(
            final AccountNumberAlreadyExistsException ex) {
        final var response = CustomErrorResponse.builder().genericMessage(ex.getMessage()).build();
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    ResponseEntity<Object> handleEntityNotFoundException(final EntityNotFoundException ex) {
        final var response = CustomErrorResponse.builder().genericMessage(ex.getMessage()).build();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    // ------------------------------
    // Privates

    private static CustomErrorResponse processErrors(
            final BindingResult result, final String genericMessage) {
        final var errorResponseBuilder = CustomErrorResponse.builder();
        errorResponseBuilder.genericMessage(genericMessage);

        result.getAllErrors()
                .forEach(
                        e -> {
                            final String field;
                            if (e instanceof FieldError) {
                                field = ((FieldError) e).getField();

                            } else {
                                field = e.getObjectName();
                            }
                            errorResponseBuilder.error(field, e.getDefaultMessage());
                        });

        return errorResponseBuilder.build();
    }

    private static CustomErrorResponse processErrors(
            final ConstraintViolationException ex, final String genericMessage) {
        final var errorsMap = new HashMap<String, String>();
        ex.getConstraintViolations()
                .forEach(
                        e ->
                                errorsMap.put(
                                        ((PathImpl) e.getPropertyPath()).getLeafNode().getName(),
                                        e.getMessage()));

        return CustomErrorResponse.builder()
                .genericMessage(genericMessage)
                .errors(errorsMap)
                .build();
    }

    @Getter
    @Builder
    @ToString
    @AllArgsConstructor(access = PRIVATE)
    @FieldDefaults(makeFinal = true, level = PRIVATE)
    private static class CustomErrorResponse implements Serializable {

        private static final long serialVersionUID = -2583227904016839753L;

        String genericMessage;

        @Singular
        @JsonInclude(NON_EMPTY)
        Map<String, String> errors;
    }
}
