package br.com.express_frete.fretesexpress.dto;

import br.com.express_frete.fretesexpress.model.enums.CategoriaVeiculo;
import br.com.express_frete.fretesexpress.model.enums.TipoCarroceria;
import lombok.Data;

@Data
public class VeiculoDTO {
    private Long id;
    private String renavam;
    private String marca;
    private Integer ano;
    private String modelo;
    private String placa;
    private String cor;
    private Double peso;
    private Double comprimento;
    private Double largura;
    private Double altura;
    private Integer quantidadeEixos;
    private Boolean possuiLona;
    private CategoriaVeiculo categoria;
    private TipoCarroceria tipoCarroceria;
}