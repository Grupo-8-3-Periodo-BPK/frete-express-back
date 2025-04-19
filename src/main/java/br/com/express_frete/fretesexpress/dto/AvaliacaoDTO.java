package br.com.express_frete.fretesexpress.dto;

import br.com.express_frete.fretesexpress.model.enums.RatingType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class AvaliacaoDTO {

    @NotNull(message = "O tipo de avaliação é obrigatório")
    private RatingType rating;

    @NotBlank(message = "O tipo de usuário avaliador é obrigatório")
    private String type; // cliente ou motorista

    private String comentario;

    @NotNull(message = "ID do motorista é obrigatório")
    private Long motoristaId;

    @NotNull(message = "ID do cliente é obrigatório")
    private Long clienteId;

    // Getters e Setters
    public RatingType getRating() {
        return rating;
    }

    public void setRating(RatingType rating) {
        this.rating = rating;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    public Long getMotoristaId() {
        return motoristaId;
    }

    public void setMotoristaId(Long motoristaId) {
        this.motoristaId = motoristaId;
    }

    public Long getClienteId() {
        return clienteId;
    }

    public void setClienteId(Long clienteId) {
        this.clienteId = clienteId;
    }
}