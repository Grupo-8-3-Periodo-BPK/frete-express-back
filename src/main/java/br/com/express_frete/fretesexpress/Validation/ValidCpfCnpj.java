package br.com.express_frete.fretesexpress.Validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = CpfCnpjValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidCpfCnpj {
    String message() default "CPF/CNPJ invalido";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}