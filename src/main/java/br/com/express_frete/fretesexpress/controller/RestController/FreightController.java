package br.com.express_frete.fretesexpress.controller.RestController;

import br.com.express_frete.fretesexpress.model.Freight;
import br.com.express_frete.fretesexpress.repository.FreightRepository;
import br.com.express_frete.fretesexpress.service.RouteService;
import br.com.express_frete.fretesexpress.dto.FreightCalculationDTO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/freights")
public class FreightController {

    @Autowired
    private FreightRepository repository;

    @Autowired
    private RouteService routeService;

    @GetMapping
    public List<Freight> listar() {
        return repository.findAll();
    }

    @PostMapping
    public ResponseEntity<?> cadastrar(@RequestBody @Valid Freight freight) {
        try {
            if (freight.getIncludeFuelCost() != null && freight.getIncludeFuelCost()) {
                FreightCalculationDTO calculation = calculateFuelCost(
                        freight.getOrigin_city() + ", " + freight.getOrigin_state(),
                        freight.getDestination_city() + ", " + freight.getDestination_state(),
                        true
                );

                freight.setFuelCost(Double.parseDouble(calculation.getFuelCost().replaceAll("[^0-9.]", "")));
            } else {
                freight.setFuelCost(0.0);
            }

            Freight savedFreight = repository.save(freight);
            return ResponseEntity.ok(savedFreight);

        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Erro ao cadastrar frete: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @GetMapping("/{id}")
    public Freight buscar(@PathVariable Long id) {
        return repository.findById(id).orElse(null);
    }

    @GetMapping("/calculate")
    public ResponseEntity<?> calculateFreight(
            @RequestParam String origin,
            @RequestParam String destination,
            @RequestParam(required = false, defaultValue = "false") boolean includeFuelCost) {

        try {
            FreightCalculationDTO calculation = calculateFuelCost(origin, destination, includeFuelCost);
            return ResponseEntity.ok(calculation);

        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Erro ao calcular frete: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    private FreightCalculationDTO calculateFuelCost(String origin, String destination, boolean includeFuelCost) {
        // Obter distância da API
        String distanceResponse = routeService.getDirections(origin, destination);

        // Extrair o valor numérico da distância
        String distanceStr = distanceResponse.replaceAll("[^0-9.]", "");
        double distanceKm = Double.parseDouble(distanceStr);

        FreightCalculationDTO calculation = new FreightCalculationDTO();
        calculation.setOrigin(origin);
        calculation.setDestination(destination);
        calculation.setDistance(String.format("%.2f km", distanceKm));
        calculation.setIncludeFuelCost(includeFuelCost);

        // Calcular custo de combustível se solicitado
        if (includeFuelCost) {
            double fuelCost = (distanceKm / 5.60) / 2; // 5.60 é o preço fixo da gasolina
            calculation.setFuelCost(String.format("R$ %.2f", fuelCost));
        }

        return calculation;
    }

    @PutMapping("/{id}")
    public Freight atualizar(@PathVariable Long id, @RequestBody @Valid Freight dados) {
        return repository.findById(id).map(frete -> {
            frete.setName(dados.getName());
            frete.setWeight(dados.getWeight());
            frete.setHeight(dados.getHeight());
            frete.setWidth(dados.getWidth());
            frete.setLength(dados.getLength());
            frete.setOrigin_city(dados.getOrigin_city());
            frete.setOrigin_state(dados.getOrigin_state());
            frete.setDestination_city(dados.getDestination_city());
            frete.setDestination_state(dados.getDestination_state());
            frete.setInitial_date(dados.getInitial_date());
            frete.setFinal_date(dados.getFinal_date());
            frete.setFuelCost(dados.getFuelCost());
            frete.setIncludeFuelCost(dados.getIncludeFuelCost());
            return repository.save(frete);
        }).orElse(null);
    }

    @DeleteMapping("/{id}")
    public void deletar(@PathVariable Long id) {
        repository.deleteById(id);
    }
}