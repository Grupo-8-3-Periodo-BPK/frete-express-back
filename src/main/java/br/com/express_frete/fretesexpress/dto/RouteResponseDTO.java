package br.com.express_frete.fretesexpress.dto;

import java.util.List;

/**
 * DTO simplificado para a resposta da rota.
 * Contém apenas a lista de coordenadas [latitude, longitude] prontas para o frontend.
 */
public class RouteResponseDTO {
    private List<List<Double>> coordinates;

    // Construtores
    public RouteResponseDTO() {
    }

    public RouteResponseDTO(List<List<Double>> coordinates) {
        this.coordinates = coordinates;
    }

    // Getters e Setters
    public List<List<Double>> getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(List<List<Double>> coordinates) {
        this.coordinates = coordinates;
    }
}