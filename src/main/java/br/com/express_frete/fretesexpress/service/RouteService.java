package br.com.express_frete.fretesexpress.service;

import br.com.express_frete.fretesexpress.dto.CoordinatesResponseDTO;
import br.com.express_frete.fretesexpress.dto.DirectionsResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.List;
import java.util.Locale;


@Service
public class RouteService {
    private final RestTemplate restTemplate;
    private final String apiKey = "5b3ce3597851110001cf6248cecae8ca5bdd43ce8feffb0d5eb95814"; // Ou injete via @Value

    public RouteService(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }
    public String getDirections(String start, String end) {
        String getCordinatesA = "https://api.openrouteservice.org/geocode/search?api_key="+ apiKey + "&text="+ start;
        String getCordinatesB = "https://api.openrouteservice.org/geocode/search?api_key="+ apiKey + "&text="+ end;

        try {
            String responseCordinatesA = restTemplate.getForObject(getCordinatesA, String.class);
            ObjectMapper objMapper = new ObjectMapper();
            CoordinatesResponseDTO cordinatesA = objMapper.readValue(responseCordinatesA, CoordinatesResponseDTO.class);

            String responseCordinatesB = restTemplate.getForObject(getCordinatesB, String.class);
            ObjectMapper objectMapper = new ObjectMapper();
            CoordinatesResponseDTO cordinatesB = objMapper.readValue(responseCordinatesB, CoordinatesResponseDTO.class);

            List<Double> cordinatesATratado = cordinatesA
                    .getFeatures().get(0)
                    .getGeometry().getCoordinates();
            List<Double> cordinatesBTratado = cordinatesB.getFeatures().get(0).getGeometry().getCoordinates();

            String startCoordinates = cordinatesATratado.get(0) + "," + cordinatesATratado.get(1);
            String endCoordinates = cordinatesBTratado.get(0) + "," + cordinatesBTratado.get(1);

            String url = "https://api.openrouteservice.org/v2/directions/driving-car?api_key=" + apiKey +
                    "&start=" + startCoordinates + "&end=" + endCoordinates;
            String response = restTemplate.getForObject(url, String.class);
                ObjectMapper objMapperSLA = new ObjectMapper();
                DirectionsResponse directionsResponse = objMapperSLA.readValue(response, DirectionsResponse.class);
                double distance = directionsResponse
                        .getFeatures().get(0)
                        .getProperties()
                        .getSegments().get(0)
                        .getDistance();

            return "Distance: " + String.format(Locale.US, "%.2f", (distance / 1000)) + "KM";

        } catch (JsonMappingException e) {
            throw new RuntimeException(e);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}