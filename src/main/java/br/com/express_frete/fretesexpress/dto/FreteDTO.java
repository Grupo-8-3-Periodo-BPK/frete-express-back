package br.com.express_frete.fretesexpress.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import br.com.express_frete.fretesexpress.model.enums.FreteStatus;

public class FreteDTO {

    private Long id;
    private String nomeProduto;
    private Double peso;
    private Double altura;
    private Double comprimento;
    private Double largura;
    private LocalDate dataInicial;
    private LocalDate dataFinal;
    private String cidadeOrigem;
    private String estadoOrigem;
    private String cidadeDestino;
    private String estadoDestino;
    private FreteStatus status;
    private LocalDateTime dataHoraAprovacaoCliente;
    private LocalDateTime dataHoraAprovacaoMotorista;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNomeProduto() {
        return nomeProduto;
    }

    public void setNomeProduto(String nomeProduto) {
        this.nomeProduto = nomeProduto;
    }

    public Double getPeso() {
        return peso;
    }

    public void setPeso(Double peso) {
        this.peso = peso;
    }

    public Double getAltura() {
        return altura;
    }

    public void setAltura(Double altura) {
        this.altura = altura;
    }

    public Double getComprimento() {
        return comprimento;
    }

    public void setComprimento(Double comprimento) {
        this.comprimento = comprimento;
    }

    public Double getLargura() {
        return largura;
    }

    public void setLargura(Double largura) {
        this.largura = largura;
    }

    public LocalDate getDataInicial() {
        return dataInicial;
    }

    public void setDataInicial(LocalDate dataInicial) {
        this.dataInicial = dataInicial;}

    public LocalDate getDataFinal() {return dataFinal;}

    public void setDataFinal(LocalDate dataFinal) {this.dataFinal = dataFinal;}

    public String getCidadeOrigem() {return cidadeOrigem;}

    public void setCidadeOrigem(String cidadeOrigem) {this.cidadeOrigem = cidadeOrigem;}

    public String getEstadoOrigem() {return estadoOrigem;}
    public void setEstadoOrigem(String estadoOrigem) {this.estadoOrigem = estadoOrigem;}

    public String getCidadeDestino() {return cidadeDestino;}
    public void setCidadeDestino(String cidadeDestino) {this.cidadeDestino = cidadeDestino;}

    public String getEstadoDestino() {return estadoDestino;}
    public void setEstadoDestino(String estadoDestino) {this.estadoDestino = estadoDestino;}

    public FreteStatus getStatus() {return status;}
    public void setStatus(FreteStatus status) {this.status = status;}

    public LocalDateTime getDataHoraAprovacaoCliente() {return dataHoraAprovacaoCliente;}

    public void setDataHoraAprovacaoCliente(LocalDateTime dataHoraAprovacaoCliente) {
        this.dataHoraAprovacaoCliente = dataHoraAprovacaoCliente;
    }

    public LocalDateTime getDataHoraAprovacaoMotorista() {
        return dataHoraAprovacaoMotorista;
    }

    public void setDataHoraAprovacaoMotorista(LocalDateTime dataHoraAprovacaoMotorista) {
        this.dataHoraAprovacaoMotorista = dataHoraAprovacaoMotorista;
    }
}
