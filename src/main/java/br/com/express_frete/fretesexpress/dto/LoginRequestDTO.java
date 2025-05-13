package br.com.express_frete.fretesexpress.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class LoginRequestDTO {

    @NotBlank(message = "O e-mail eh obrigatorio")
    @Email(message = "E-mail inválido")
    private String email;

    @NotBlank(message = "A senha eh obrigatoria")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,}$",
            message = "A senha deve ter no minimo 8 caracteres, incluindo letras, numeros e simbolos")
    private String senha;
}