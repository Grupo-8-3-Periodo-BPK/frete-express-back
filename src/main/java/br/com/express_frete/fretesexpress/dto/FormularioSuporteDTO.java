package br.com.express_frete.fretesexpress.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class FormularioSuporteDTO {

    private Long id;

    @NotBlank(message = "O nome do solicitante eh obrigatorio")
    @Size(max = 100, message = "O nome deve ter no máximo 100 caracteres")
    private String nomeSolicitante;

    @NotBlank(message = "O e-mail para retorno eh obrigatorio")
    @Email(message = "E-mail invalido")
    @Size(max = 100, message = "O e-mail deve ter no maximo 100 caracteres")
    private String emailRetorno;

    @NotBlank(message = "O assunto eh obrigatorio")
    @Size(max = 200, message = "O assunto deve ter no maximo 200 caracteres")
    private String assunto;

    @NotBlank(message = "A descricao do problema eh obrigatoria")
    private String descricaoProblema;

    private LocalDateTime dataEnvio;
}