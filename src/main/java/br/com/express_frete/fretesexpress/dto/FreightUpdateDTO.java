package br.com.express_frete.fretesexpress.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.LocalDate;

public class FreightUpdateDTO {

    @NotBlank
    private String name;

    @NotNull
    private Double price;

    @Positive
    private Double weight;

    @Positive
    private Double height;

    @Positive
    private Double length;

    @Positive
    private Double width;

    @NotNull
    private LocalDate initial_date;

    @NotNull
    private LocalDate final_date;

    @NotBlank
    private String origin_city;

    @NotBlank
    private String origin_state;

    @NotBlank
    private String destination_city;

    @NotBlank
    private String destination_state;

    // Getters and Setters

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public Double getHeight() {
        return height;
    }

    public void setHeight(Double height) {
        this.height = height;
    }

    public Double getLength() {
        return length;
    }

    public void setLength(Double length) {
        this.length = length;
    }

    public Double getWidth() {
        return width;
    }

    public void setWidth(Double width) {
        this.width = width;
    }

    public LocalDate getInitial_date() {
        return initial_date;
    }

    public void setInitial_date(LocalDate initial_date) {
        this.initial_date = initial_date;
    }

    public LocalDate getFinal_date() {
        return final_date;
    }

    public void setFinal_date(LocalDate final_date) {
        this.final_date = final_date;
    }

    public String getOrigin_city() {
        return origin_city;
    }

    public void setOrigin_city(String origin_city) {
        this.origin_city = origin_city;
    }

    public String getOrigin_state() {
        return origin_state;
    }

    public void setOrigin_state(String origin_state) {
        this.origin_state = origin_state;
    }

    public String getDestination_city() {
        return destination_city;
    }

    public void setDestination_city(String destination_city) {
        this.destination_city = destination_city;
    }

    public String getDestination_state() {
        return destination_state;
    }

    public void setDestination_state(String destination_state) {
        this.destination_state = destination_state;
    }
}