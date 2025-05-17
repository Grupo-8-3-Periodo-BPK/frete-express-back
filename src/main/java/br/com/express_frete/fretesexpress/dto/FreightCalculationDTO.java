package br.com.express_frete.fretesexpress.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class FreightCalculationDTO {
    private String distance;

    @JsonProperty("fuel_cost")
    private String fuelCost;

    @JsonProperty("include_fuel_cost")
    private Boolean includeFuelCost;

    @JsonProperty("origin")
    private String origin;

    @JsonProperty("destination")
    private String destination;

    // Getters e Setters
    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getFuelCost() {
        return fuelCost;
    }

    public void setFuelCost(String fuelCost) {
        this.fuelCost = fuelCost;
    }

    public Boolean getIncludeFuelCost() {
        return includeFuelCost;
    }

    public void setIncludeFuelCost(Boolean includeFuelCost) {
        this.includeFuelCost = includeFuelCost;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }
}