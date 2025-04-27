package br.com.express_frete.fretesexpress.dto;

import br.com.express_frete.fretesexpress.model.enums.CategoriaVeiculo;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class OrcamentoFreteDTO {

    private Long id;

    @NotBlank(message = "O tipo de carga eh obrigatorio")
    @Size(max = 100, message = "O tipo de carga deve ter no maximo 100 caracteres")
    private String tipoCarga;

    @NotNull(message = "O peso eh obrigatorio")
    @Positive(message = "O peso deve ser um valor positivo")
    private Double peso;

    @NotNull(message = "A altura eh obrigatoria")
    @Positive(message = "A altura deve ser um valor positivo")
    private Double altura;

    @NotNull(message = "A largura eh obrigatoria")
    @Positive(message = "A largura deve ser um valor positivo")
    private Double largura;

    @NotNull(message = "O comprimento eh obrigatorio")
    @Positive(message = "O comprimento deve ser um valor positivo")
    private Double comprimento;

    @NotBlank(message = "A cidade de origem eh obrigatoria")
    @Size(max = 100, message = "A cidade de origem deve ter no maximo 100 caracteres")
    private String cidadeOrigem;

    @NotBlank(message = "O estado de origem eh obrigatorio")
    @Size(max = 2, message = "O estado de origem deve ter 2 caracteres (UF)")
    private String estadoOrigem;

    @NotBlank(message = "A cidade de destino eh obrigatoria")
    @Size(max = 100, message = "A cidade de destino deve ter no maximo 100 caracteres")
    private String cidadeDestino;

    @NotBlank(message = "O estado de destino eh obrigatorio")
    @Size(max = 2, message = "O estado de destino deve ter 2 caracteres (UF)")
    private String estadoDestino;

    @NotNull(message = "O tipo de veículo eh obrigatorio")
    private CategoriaVeiculo tipoVeiculo;

    private Double valorEstimado;
}