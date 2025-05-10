package br.com.express_frete.fretesexpress.controller;
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
    public String getDirections(@RequestParam String start, @RequestParam String end) {
        return routeService.getDirections(start, end);
    }

}