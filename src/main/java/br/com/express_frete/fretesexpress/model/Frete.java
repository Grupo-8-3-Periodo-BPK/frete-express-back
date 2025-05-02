package br.com.express_frete.fretesexpress.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import br.com.express_frete.fretesexpress.model.enums.FreteStatus;

@Entity
@Table(name = "frete")
public class Frete {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String nomeProduto;

    @Positive
    private Double peso;

    @Positive
    private Double altura;

    @Positive
    private Double comprimento;

    @Positive
    private Double largura;

    @NotNull
    private LocalDate dataInicial;

    @NotNull
    private LocalDate dataFinal;

    @NotBlank
    private String cidadeOrigem;

    @NotBlank
    private String estadoOrigem;

    @NotBlank
    private String cidadeDestino;

    @NotBlank
    private String estadoDestino;

    @NotBlank
    private String tipoCaminhao;

    @NotBlank
    private String tipoCarga;

    @Enumerated(EnumType.STRING)
    private FreteStatus status;

    private LocalDateTime dataHoraAprovacaoCliente;
    private LocalDateTime dataHoraAprovacaoMotorista;

// Getters e Setters

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNomeProduto() { return nomeProduto; }
    public void setNomeProduto(String nomeProduto) { this.nomeProduto = nomeProduto; }

    public Double getPeso() { return peso; }
    public void setPeso(Double peso) { this.peso = peso; }

    public Double getAltura() { return altura; }
    public void setAltura(Double altura) { this.altura = altura; }

    public Double getComprimento() { return comprimento; }
    public void setComprimento(Double comprimento) { this.comprimento = comprimento; }

    public Double getLargura() { return largura; }
    public void setLargura(Double largura) { this.largura = largura; }

    public LocalDate getDataInicial() { return dataInicial; }
    public void setDataInicial(LocalDate dataInicial) { this.dataInicial = dataInicial; }

    public LocalDate getDataFinal() { return dataFinal; }
    public void setDataFinal(LocalDate dataFinal) { this.dataFinal = dataFinal; }

    public String getCidadeOrigem() { return cidadeOrigem; }
    public void setCidadeOrigem(String cidadeOrigem) { this.cidadeOrigem = cidadeOrigem; }

    public String getEstadoOrigem() { return estadoOrigem; }
    public void setEstadoOrigem(String estadoOrigem) { this.estadoOrigem = estadoOrigem; }

    public String getCidadeDestino() { return cidadeDestino; }
    public void setCidadeDestino(String cidadeDestino) { this.cidadeDestino = cidadeDestino; }

    public String getEstadoDestino() { return estadoDestino; }
    public void setEstadoDestino(String estadoDestino) { this.estadoDestino = estadoDestino; }

    public @NotBlank String getTipoCaminhao() {return tipoCaminhao;}
    public void setTipoCaminhao(@NotBlank String tipoCaminhao) {this.tipoCaminhao = tipoCaminhao;}

    public @NotBlank String getTipoCarga() {return tipoCarga;}
    public void setTipoCarga(@NotBlank String tipoCarga) {this.tipoCarga = tipoCarga;}

    public FreteStatus getStatus() { return status; }
    public void setStatus(FreteStatus status) { this.status = status; }
}
