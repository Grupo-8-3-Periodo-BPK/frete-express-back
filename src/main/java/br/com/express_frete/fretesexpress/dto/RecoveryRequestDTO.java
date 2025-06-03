package br.com.express_frete.fretesexpress.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RecoveryRequestDTO {

    @NotBlank(message = "O e-mail ou telefone é obrigatório")
    private String emailOrPhone;
}
