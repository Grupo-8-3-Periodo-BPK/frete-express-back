package br.com.express_frete.fretesexpress.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.br.CPF;

public class UserDTO {

    @NotBlank
    private String name;

    @Email
    private String email;

    @CPF(message = "Invalid CPF")
    private String cpf;

    @NotBlank
    private String password;
}

