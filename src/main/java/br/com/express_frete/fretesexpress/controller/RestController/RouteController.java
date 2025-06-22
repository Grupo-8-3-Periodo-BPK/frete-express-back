package br.com.express_frete.fretesexpress.controller.RestController;

import br.com.express_frete.fretesexpress.dto.RouteResponseDTO;
import br.com.express_frete.fretesexpress.service.RouteService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class RouteController {
    private final RouteService routeService;

    public RouteController(RouteService routeService) {
        this.routeService = routeService;
    }

    @GetMapping("/directions")
    public RouteResponseDTO getDirections(@RequestParam String start, @RequestParam String end) {
        // Agora o retorno é específico e mais seguro
        return routeService.getDirections(start, end);
    }

    @GetMapping("/geocode")
    public String geocode(@RequestParam String address) {
        return routeService.getCoordinates(address);
    }
}