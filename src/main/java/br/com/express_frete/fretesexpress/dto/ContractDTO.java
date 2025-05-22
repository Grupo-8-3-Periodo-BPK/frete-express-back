package br.com.express_frete.fretesexpress.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class ContractDTO {
    private Long id;

    @NotBlank(message = "O nome do contrato é obrigatório")
    @Size(max = 45, message = "O nome deve ter no máximo 45 caracteres")
    private String name;

    @NotNull(message = "O cliente é obrigatório")
    private Long clientId;

    @NotNull(message = "O motorista é obrigatório")
    private Long driverId;

    @NotNull(message = "O frete é obrigatório")
    private Long freightId;

    private Boolean driverAccepted = false;

    private Boolean clientAccepted = false;

    private Long userId;

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getClientId() {
        return clientId;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }

    public Long getDriverId() {
        return driverId;
    }

    public void setDriverId(Long driverId) {
        this.driverId = driverId;
    }

    public Long getFreightId() {
        return freightId;
    }

    public void setFreightId(Long freightId) {
        this.freightId = freightId;
    }

    public Boolean getDriverAccepted() {
        return driverAccepted;
    }

    public void setDriverAccepted(Boolean driverAccepted) {
        this.driverAccepted = driverAccepted;
    }

    public Boolean getClientAccepted() {
        return clientAccepted;
    }

    public void setClientAccepted(Boolean clientAccepted) {
        this.clientAccepted = clientAccepted;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}