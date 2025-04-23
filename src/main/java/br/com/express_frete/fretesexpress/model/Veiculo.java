package br.com.express_frete.fretesexpress.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import br.com.express_frete.fretesexpress.model.enums.CategoriaVeiculo;
import br.com.express_frete.fretesexpress.model.enums.TipoCarroceria;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "veiculo")
public class Veiculo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "O número do Renavam eh obrigatorio")
    @Pattern(regexp = "\\d{9}|\\d{11}", message = "Renavam inválido. Deve ter 9 ou 11 digitos")
    private String renavam;

    @NotBlank(message = "A marca eh obrigatoria")
    @Size(max = 50, message = "A marca deve ter no maximo 50 caracteres")
    private String marca;

    @Min(value = 1800, message = "O ano deve ser maior ou igual a 1800")
    @Max(value = 2100, message = "O ano deve ser menor ou igual a 2100")
    private Integer ano;

    @NotBlank(message = "O modelo eh obrigatorio")
    @Size(max = 50, message = "O modelo deve ter no maximo 50 caracteres")
    private String modelo;

    @NotBlank(message = "A placa do veiculo eh obrigatoria")
    @Pattern(regexp = "[A-Z]{3}[0-9][0-9A-Z][0-9]{2}|[A-Z]{3}-[0-9]{4}",
            message = "Placa invalida. Use o formato Mercosul (AAA0A00)")
    @Column(unique = true)
    private String placa;

    @NotBlank(message = "A cor eh obrigatoria")
    @Size(max = 30, message = "A cor deve ter no maximo 30 caracteres")
    private String cor;

    @PositiveOrZero(message = "O peso deve ser maior ou igual a 0")
    private Double peso;

    @PositiveOrZero(message = "O comprimento deve ser maior ou igual a 0")
    private Double comprimento;

    @PositiveOrZero(message = "A largura deve ser maior ou igual a 0")
    private Double largura;

    @PositiveOrZero(message = "A altura deve ser maior ou igual a 0")
    private Double altura;

    @Min(value = 1, message = "A quantidade de eixos deve ser pelo menos 1")
    private Integer quantidadeEixos;

    @NotNull(message = "Eh obrigatorio informar se possui lona")
    private Boolean possuiLona;

    @NotNull(message = "A categoria eh obrigatoria")
    @Enumerated(EnumType.STRING)
    private CategoriaVeiculo categoria;

    @NotNull(message = "O tipo de carroceria eh obrigatorio")
    @Enumerated(EnumType.STRING)
    private TipoCarroceria tipoCarroceria;

    @ManyToOne
    @JoinColumn(name = "motorista_id", nullable = false)
    @JsonBackReference
    private Motorista motorista;
}