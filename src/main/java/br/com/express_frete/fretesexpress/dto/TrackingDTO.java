package br.com.express_frete.fretesexpress.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class TrackingDTO {
    private Long id;

    @NotNull(message = "O frete é obrigatório")
    private Long freightId;

    @Size(max = 255, message = "A localização atual deve ter no máximo 255 caracteres")
    private String currentLocation;

    @NotNull(message = "O contrato é obrigatório")
    private Long contractId;

    private Long contractUserId;

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getFreightId() {
        return freightId;
    }

    public void setFreightId(Long freightId) {
        this.freightId = freightId;
    }

    public String getCurrentLocation() {
        return currentLocation;
    }

    public void setCurrentLocation(String currentLocation) {
        this.currentLocation = currentLocation;
    }

    public Long getContractId() {
        return contractId;
    }

    public void setContractId(Long contractId) {
        this.contractId = contractId;
    }

    public Long getContractUserId() {
        return contractUserId;
    }

    public void setContractUserId(Long contractUserId) {
        this.contractUserId = contractUserId;
    }
}