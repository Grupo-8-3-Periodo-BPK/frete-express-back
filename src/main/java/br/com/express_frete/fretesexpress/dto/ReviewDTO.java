package br.com.express_frete.fretesexpress.dto;

import br.com.express_frete.fretesexpress.model.enums.RatingType;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class ReviewDTO {

    @NotNull(message = "The rating type is required")
    private RatingType rating;

    @NotBlank(message = "The type of user evaluator is required")
    private String type; // client or driver

    private String comment;

    @NotNull(message = "The driver ID is required")
    @JsonProperty("driver_id")
    private Long driver_id;

    @NotNull(message = "The client ID is required")
    @JsonProperty("client_id")
    private Long client_id;

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

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    @JsonProperty("driver_id")
    public Long getDriverId() {
        return driver_id;
    }

    @JsonProperty("driver_id")
    public void setDriverId(Long driver_id) {
        this.driver_id = driver_id;
    }

    @JsonProperty("client_id")
    public Long getClientId() {
        return client_id;
    }

    @JsonProperty("client_id")
    public void setClientId(Long client_id) {
        this.client_id = client_id;
    }
}