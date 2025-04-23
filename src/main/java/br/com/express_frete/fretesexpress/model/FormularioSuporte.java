package br.com.express_frete.fretesexpress.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "formulario_suporte")
public class FormularioSuporte {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "O nome do solicitante eh obrigatorio")
    @Size(max = 100, message = "O nome deve ter no maximo 100 caracteres")
    @Column(name = "nome_solicitante")
    private String nomeSolicitante;

    @NotBlank(message = "O e-mail para retorno eh obrigatorio")
    @Email(message = "E-mail invalido")
    @Size(max = 100, message = "O e-mail deve ter no maximo 100 caracteres")
    @Column(name = "email_retorno")
    private String emailRetorno;

    @NotBlank(message = "O assunto eh obrigatorio")
    @Size(max = 200, message = "O assunto deve ter no maximo 200 caracteres")
    private String assunto;

    @NotBlank(message = "A descricao do problema eh obrigatoria")
    @Column(name = "descricao_problema")
    private String descricaoProblema;

    @NotNull(message = "A data de envio eh obrigatoria")
    @Column(name = "data_envio")
    private LocalDateTime dataEnvio;
}