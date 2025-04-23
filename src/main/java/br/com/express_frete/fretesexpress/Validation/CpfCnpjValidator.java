package br.com.express_frete.fretesexpress.Validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CpfCnpjValidator implements ConstraintValidator<ValidCpfCnpj, String> {

    private static final Logger logger = LoggerFactory.getLogger(CpfCnpjValidator.class);

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        logger.info("Validando CPF/CNPJ: {}", value);
        if (value == null) {
            logger.warn("CPF/CNPJ eh nulo");
            return false;
        }

        String cleaned = value.replaceAll("[^0-9]", "");
        logger.debug("CPF/CNPJ limpo: {}", cleaned);
        if (cleaned.length() == 11) {
            boolean isValid = isValidCpf(cleaned);
            logger.info("CPF {} eh valido: {}", cleaned, isValid);
            return isValid;
        }
        if (cleaned.length() == 14) {
            boolean isValid = isValidCnpj(cleaned);
            logger.info("CNPJ {} eh valido: {}", cleaned, isValid);
            return isValid;
        }
        logger.warn("CPF/CNPJ {} tem tamanho invalido: {}", cleaned, cleaned.length());
        return false;
    }

    private boolean isValidCpf(String cpf) {
        if (cpf.matches("(\\d)\\1{10}")) {
            logger.warn("CPF {} eh invalido: todos os digitos iguais", cpf);
            return false; // Ex.: 11111111111
        }
        int[] digits = cpf.chars().map(c -> c - '0').toArray();
        boolean isValid = calculateCpfDigit(digits, 9) == digits[9] && calculateCpfDigit(digits, 10) == digits[10];
        logger.debug("CPF {} - Digito 1: esperado {}, calculado {}; Dígito 2: esperado {}, calculado {}",
                cpf, digits[9], calculateCpfDigit(digits, 9), digits[10], calculateCpfDigit(digits, 10));
        return isValid;
    }

    private int calculateCpfDigit(int[] digits, int length) {
        int sum = 0;
        for (int i = 0; i < length; i++) sum += digits[i] * (length + 1 - i);
        int remainder = sum % 11;
        return remainder < 2 ? 0 : 11 - remainder;
    }

    private boolean isValidCnpj(String cnpj) {
        if (cnpj.matches("(\\d)\\1{13}")) {
            logger.warn("CNPJ {} eh invalido: todos os digitos iguais", cnpj);
            return false;
        }
        int[] digits = cnpj.chars().map(c -> c - '0').toArray();
        boolean isValid = calculateCnpjDigit(digits, 12) == digits[12] && calculateCnpjDigit(digits, 13) == digits[13];
        logger.debug("CNPJ {} - Digito 1: esperado {}, calculado {}; Digito 2: esperado {}, calculado {}",
                cnpj, digits[12], calculateCnpjDigit(digits, 12), digits[13], calculateCnpjDigit(digits, 13));
        return isValid;
    }

    private int calculateCnpjDigit(int[] digits, int length) {
        int[] weights = length == 12 ? new int[]{5,4,3,2,9,8,7,6,5,4,3,2} : new int[]{6,5,4,3,2,9,8,7,6,5,4,3,2};
        int sum = 0;
        for (int i = 0; i < length; i++) sum += digits[i] * weights[i];
        int remainder = sum % 11;
        return remainder < 2 ? 0 : 11 - remainder;
    }
}