package br.com.express_frete.fretesexpress.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class TokenValidationDTO {

    @NotBlank(message = "O token é obrigatório")
    private String token;
}
