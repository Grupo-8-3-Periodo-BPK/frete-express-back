package br.com.express_frete.fretesexpress.service;

import br.com.express_frete.fretesexpress.dto.RouteResponseDTO;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Service
public class RouteService {
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper; // Adicionado para processar JSON

    @Value("${openrouteservice.apiKey}")
    private String apiKey;

    public RouteService(RestTemplateBuilder restTemplateBuilder, ObjectMapper objectMapper) {
        this.restTemplate = restTemplateBuilder.build();
        this.objectMapper = objectMapper;
    }

    /**
     * Busca as direções, processa a resposta e retorna um DTO simplificado com as
     * coordenadas.
     */
    public RouteResponseDTO getDirections(String start, String end) {
        // A API Key não vai mais na URL por segurança
        String url = String.format(
                "https://api.openrouteservice.org/v2/directions/driving-car?start=%s&end=%s",
                start, end);

        try {
            // 1. Configura o cabeçalho de Autorização para a API Key
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", apiKey);
            HttpEntity<String> entity = new HttpEntity<>(headers);

            // 2. Faz a chamada à API externa
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
            JsonNode root = objectMapper.readTree(response.getBody());

            // 3. Extrai as coordenadas do JSON de forma segura
            JsonNode features = root.path("features");
            if (features.isMissingNode() || !features.isArray() || features.isEmpty()) {
                // Se não houver rotas, retorna uma lista vazia educadamente.
                return new RouteResponseDTO(new ArrayList<>());
            }
            JsonNode coordsNode = features.get(0).path("geometry").path("coordinates");

            // 4. Inverte as coordenadas [lon, lat] para [lat, lon] para o Leaflet
            List<List<Double>> invertedCoords = new ArrayList<>();
            if (coordsNode.isArray()) {
                for (JsonNode coord : coordsNode) {
                    invertedCoords.add(List.of(coord.get(1).asDouble(), coord.get(0).asDouble()));
                }
            }

            // 5. Retorna o DTO simplificado com os dados prontos para o frontend
            return new RouteResponseDTO(invertedCoords);

        } catch (Exception e) {
            System.err.println("Erro ao buscar e processar direções da API externa: " + e.getMessage());
            throw new RuntimeException(
                    "O serviço de rotas externo está indisponível no momento. Tente novamente mais tarde.", e);
        }
    }

    /**
     * Busca as coordenadas para um endereço (Geocoding).
     */
    public String getCoordinates(String address) {
        // A API Key não vai mais na URL por segurança
        String url = String.format("https://api.openrouteservice.org/geocode/search?text=%s", address);

        try {
            // Configura o cabeçalho de Autorização
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", apiKey);
            HttpEntity<String> entity = new HttpEntity<>(headers);

            // Faz a chamada
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
            JsonNode root = objectMapper.readTree(response.getBody());

            if (root != null && root.has("features") && root.get("features").isArray()
                    && root.get("features").size() > 0) {
                JsonNode coordinates = root.get("features").get(0).get("geometry").get("coordinates");
                // Retorna "longitude,latitude"
                return coordinates.get(0).asText() + "," + coordinates.get(1).asText();
            } else {
                throw new RuntimeException("Não foi possível encontrar coordenadas para o endereço: " + address);
            }
        } catch (Exception e) {
            System.err.println("Erro ao processar geocodificação para: " + address);
            throw new RuntimeException("Falha ao buscar coordenadas. Causa: " + e.getMessage(), e);
        }
    }
}