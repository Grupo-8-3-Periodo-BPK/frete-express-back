package br.com.express_frete.fretesexpress.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public class ContractDTO {
    private Long id;

    @NotNull(message = "O cliente é obrigatório")
    private Long client_id;

    @NotNull(message = "O motorista é obrigatório")
    private Long driver_id;

    @NotNull(message = "O frete é obrigatório")
    private Long freight_id;

    @NotNull(message = "O veículo é obrigatório")
    private Long vehicle_id;

    @NotNull(message = "A data de coleta é obrigatória")
    private LocalDate pickup_date;

    @NotNull(message = "A data de entrega é obrigatória")
    private LocalDate delivery_date;

    @NotNull(message = "O valor acordado é obrigatório")
    private Double agreed_value;

    private Boolean driver_accepted = true;

    private Boolean client_accepted = false;

    private String display_name;

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getClient_id() {
        return client_id;
    }

    public void setClient_id(Long client_id) {
        this.client_id = client_id;
    }

    public Long getDriver_id() {
        return driver_id;
    }

    public void setDriver_id(Long driver_id) {
        this.driver_id = driver_id;
    }

    public Long getFreight_id() {
        return freight_id;
    }

    public void setFreight_id(Long freight_id) {
        this.freight_id = freight_id;
    }

    public Long getVehicle_id() {
        return vehicle_id;
    }

    public void setVehicle_id(Long vehicle_id) {
        this.vehicle_id = vehicle_id;
    }

    public LocalDate getPickup_date() {
        return pickup_date;
    }

    public void setPickup_date(LocalDate pickup_date) {
        this.pickup_date = pickup_date;
    }

    public LocalDate getDelivery_date() {
        return delivery_date;
    }

    public void setDelivery_date(LocalDate delivery_date) {
        this.delivery_date = delivery_date;
    }

    public Double getAgreed_value() {
        return agreed_value;
    }

    public void setAgreed_value(Double agreed_value) {
        this.agreed_value = agreed_value;
    }

    public Boolean getDriver_accepted() {
        return driver_accepted;
    }

    public void setDriver_accepted(Boolean driver_accepted) {
        this.driver_accepted = driver_accepted;
    }

    public Boolean getClient_accepted() {
        return client_accepted;
    }

    public void setClient_accepted(Boolean client_accepted) {
        this.client_accepted = client_accepted;
    }

    public String getDisplay_name() {
        return display_name;
    }

    public void setDisplay_name(String display_name) {
        this.display_name = display_name;
    }
}