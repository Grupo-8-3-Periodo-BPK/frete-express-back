package br.com.express_frete.fretesexpress.dto;

import br.com.express_frete.fretesexpress.model.enums.Status;
import java.time.LocalDate;

public class ContractResponseDTO {

    private Long id;
    private String displayName;
    private Double agreedValue;
    private Status status;
    private LocalDate pickupDate;
    private LocalDate deliveryDate;
    private String driverName;
    private String clientName;
    private Long freightId;
    private Long vehicleId;
    private Long driverId;
    private Long clientId;

    public ContractResponseDTO(Long id, String displayName, Double agreedValue, Status status,
            LocalDate pickupDate, LocalDate deliveryDate, String driverName, String clientName,
            Long freightId, Long vehicleId, Long driverId, Long clientId) {
        this.id = id;
        this.displayName = displayName;
        this.agreedValue = agreedValue;
        this.status = status;
        this.pickupDate = pickupDate;
        this.deliveryDate = deliveryDate;
        this.driverName = driverName;
        this.clientName = clientName;
        this.freightId = freightId;
        this.vehicleId = vehicleId;
        this.driverId = driverId;
        this.clientId = clientId;
    }

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public Double getAgreedValue() {
        return agreedValue;
    }

    public void setAgreedValue(Double agreedValue) {
        this.agreedValue = agreedValue;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public LocalDate getPickupDate() {
        return pickupDate;
    }

    public void setPickupDate(LocalDate pickupDate) {
        this.pickupDate = pickupDate;
    }

    public LocalDate getDeliveryDate() {
        return deliveryDate;
    }

    public void setDeliveryDate(LocalDate deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public Long getFreightId() {
        return freightId;
    }

    public void setFreightId(Long freightId) {
        this.freightId = freightId;
    }

    public Long getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(Long vehicleId) {
        this.vehicleId = vehicleId;
    }

    public Long getDriverId() {
        return driverId;
    }

    public void setDriverId(Long driverId) {
        this.driverId = driverId;
    }

    public Long getClientId() {
        return clientId;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }
}