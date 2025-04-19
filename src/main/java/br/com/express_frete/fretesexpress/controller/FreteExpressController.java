package br.com.express_frete.fretesexpress.controller;

import br.com.express_frete.fretesexpress.model.Frete;
import br.com.express_frete.fretesexpress.repository.FreteRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/fretes")
public class FreteExpressController {

    @Autowired
    private FreteRepository repository;

    @GetMapping("/novo")
    public String formNovoFrete(Model model) {
        model.addAttribute("frete", new Frete());
        return "createFrete";
    }

    @PostMapping
    public String salvarFrete(@ModelAttribute("frete") @Valid Frete frete) {
        repository.save(frete);
        return "redirect:/fretes/listar";
    }

    @GetMapping("/listar")
    public String listarFretes(Model model) {
        model.addAttribute("fretes", repository.findAll());
        return "listarFrete";
    }

    @GetMapping("/deletar/{id}")
    public String deletar(@PathVariable Long id) {
        repository.deleteById(id);
        return "redirect:/fretes/listar";
    }



}
