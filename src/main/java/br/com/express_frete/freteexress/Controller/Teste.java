package br.com.express_frete.freteexress.Controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class Teste{
    @GetMapping("/hello")
    public String Hello(){
        return "vamo gremioooo";
    }
}