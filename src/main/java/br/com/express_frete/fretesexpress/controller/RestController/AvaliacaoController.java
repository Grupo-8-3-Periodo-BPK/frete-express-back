package br.com.express_frete.fretesexpress.controller.RestController;

import br.com.express_frete.fretesexpress.dto.AvaliacaoDTO;
import br.com.express_frete.fretesexpress.model.Avaliacao;
import br.com.express_frete.fretesexpress.service.AvaliacaoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/avaliacoes")
public class AvaliacaoController {

    @Autowired
    private AvaliacaoService avaliacaoService;

    @PostMapping
    public ResponseEntity<AvaliacaoDTO> criarAvaliacao(@Valid @RequestBody AvaliacaoDTO avaliacaoDTO) {
        Avaliacao novaAvaliacao = avaliacaoService.criarAvaliacao(avaliacaoDTO);

        AvaliacaoDTO resposta = new AvaliacaoDTO();
        resposta.setRating(novaAvaliacao.getRating());
        resposta.setType(novaAvaliacao.getType());
        resposta.setComentario(novaAvaliacao.getComentario());
        resposta.setMotoristaId(novaAvaliacao.getMotorista().getId());
        resposta.setClienteId(novaAvaliacao.getCliente().getId());

        return new ResponseEntity<>(resposta, HttpStatus.CREATED);
    }


    @GetMapping("/motorista/{id}")
    public ResponseEntity<List<Avaliacao>> buscarAvaliacoesPorMotorista(@PathVariable Long id) {
        List<Avaliacao> avaliacoes = avaliacaoService.buscarAvaliacoesPorMotorista(id);
        return ResponseEntity.ok(avaliacoes);
    }

    @GetMapping("/cliente/{id}")
    public ResponseEntity<List<Avaliacao>> buscarAvaliacoesPorCliente(@PathVariable Long id) {
        List<Avaliacao> avaliacoes = avaliacaoService.buscarAvaliacoesPorCliente(id);
        return ResponseEntity.ok(avaliacoes);
    }

    @GetMapping("/feitas/{usuarioId}")
    public ResponseEntity<List<Avaliacao>> buscarAvaliacoesFeitas(@PathVariable Long usuarioId) {
        List<Avaliacao> avaliacoes = avaliacaoService.buscarAvaliacoesFeitas(usuarioId);
        return ResponseEntity.ok(avaliacoes);
    }

    @GetMapping("/recebidas/{usuarioId}")
    public ResponseEntity<List<Avaliacao>> buscarAvaliacoesRecebidas(@PathVariable Long usuarioId) {
        List<Avaliacao> avaliacoes = avaliacaoService.buscarAvaliacoesRecebidas(usuarioId);
        return ResponseEntity.ok(avaliacoes);
    }

    @GetMapping("/total-feitas/{usuarioId}")
    public ResponseEntity<Map<String, Integer>> buscarTotalAvaliacoesFeitas(@PathVariable Long usuarioId) {
        Integer total = avaliacaoService.buscarTotalAvaliacoesFeitas(usuarioId);
        Map<String, Integer> response = new HashMap<>();
        response.put("totalAvaliacoesFeitas", total);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/total-recebidas/{usuarioId}")
    public ResponseEntity<Map<String, Integer>> buscarTotalAvaliacoesRecebidas(@PathVariable Long usuarioId) {
        Integer total = avaliacaoService.buscarTotalAvaliacoesRecebidas(usuarioId);
        Map<String, Integer> response = new HashMap<>();
        response.put("totalAvaliacoesRecebidas", total);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/resumo/{usuarioId}")
    public ResponseEntity<Map<String, Integer>> buscarResumoAvaliacoes(@PathVariable Long usuarioId) {
        Integer totalFeitas = avaliacaoService.buscarTotalAvaliacoesFeitas(usuarioId);
        Integer totalRecebidas = avaliacaoService.buscarTotalAvaliacoesRecebidas(usuarioId);

        Map<String, Integer> response = new HashMap<>();
        response.put("totalAvaliacoesFeitas", totalFeitas);
        response.put("totalAvaliacoesRecebidas", totalRecebidas);

        return ResponseEntity.ok(response);
    }
}