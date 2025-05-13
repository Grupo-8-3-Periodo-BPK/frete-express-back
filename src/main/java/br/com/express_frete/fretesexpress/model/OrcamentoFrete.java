package br.com.express_frete.fretesexpress.model;

import br.com.express_frete.fretesexpress.model.enums.CategoriaVeiculo;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "orcamento_frete")
public class OrcamentoFrete {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String tipoCarga;

    @Column(nullable = false)
    private Double peso;

    @Column(nullable = false)
    private Double altura;

    @Column(nullable = false)
    private Double largura;

    @Column(nullable = false)
    private Double comprimento;

    @Column(nullable = false)
    private String cidadeOrigem;

    @Column(nullable = false)
    private String estadoOrigem;

    @Column(nullable = false)
    private String cidadeDestino;

    @Column(nullable = false)
    private String estadoDestino;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CategoriaVeiculo tipoVeiculo;

    @Column(nullable = false)
    private Double valorEstimado;

    @Column(nullable = false)
    private LocalDateTime dataSolicitacao;
}