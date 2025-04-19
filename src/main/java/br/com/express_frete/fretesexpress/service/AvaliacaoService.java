package br.com.express_frete.fretesexpress.service;

import br.com.express_frete.fretesexpress.dto.AvaliacaoDTO;
import br.com.express_frete.fretesexpress.model.Avaliacao;
import br.com.express_frete.fretesexpress.model.Usuario;
import br.com.express_frete.fretesexpress.model.enums.Role;
import br.com.express_frete.fretesexpress.repository.AvaliacaoRepository;
import br.com.express_frete.fretesexpress.repository.UsuarioRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AvaliacaoService {

    @Autowired
    private AvaliacaoRepository avaliacaoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Transactional
    public Avaliacao criarAvaliacao(AvaliacaoDTO avaliacaoDTO) {
        // Validar se os usuários existem
        Usuario motorista = usuarioRepository.findById(avaliacaoDTO.getMotoristaId())
                .orElseThrow(() -> new EntityNotFoundException("Motorista não encontrado"));

        Usuario cliente = usuarioRepository.findById(avaliacaoDTO.getClienteId())
                .orElseThrow(() -> new EntityNotFoundException("Cliente não encontrado"));

        // Validar os papéis dos usuários
        if (motorista.getRole() != Role.MOTORISTA) {
            throw new IllegalArgumentException("Usuário informado como motorista não possui esse papel");
        }

        if (cliente.getRole() != Role.CLIENTE) {
            throw new IllegalArgumentException("Usuário informado como cliente não possui esse papel");
        }

        // Validar o tipo de avaliação (quem está avaliando quem)
        if (!avaliacaoDTO.getType().equals("cliente") && !avaliacaoDTO.getType().equals("motorista")) {
            throw new IllegalArgumentException("Tipo de avaliação deve ser 'cliente' ou 'motorista'");
        }

        // Criar a avaliação
        Avaliacao avaliacao = new Avaliacao();
        avaliacao.setRating(avaliacaoDTO.getRating());
        avaliacao.setType(avaliacaoDTO.getType());
        avaliacao.setComentario(avaliacaoDTO.getComentario());
        avaliacao.setMotorista(motorista);
        avaliacao.setCliente(cliente);

        // Incrementar os contadores de avaliações
        if (avaliacaoDTO.getType().equals("cliente")) {
            // Cliente está avaliando motorista
            cliente.incrementarAvaliacoesFeitas();
            motorista.incrementarAvaliacoesRecebidas();
        } else {
            // Motorista está avaliando cliente
            motorista.incrementarAvaliacoesFeitas();
            cliente.incrementarAvaliacoesRecebidas();
        }

        // Salvar os usuários atualizados
        usuarioRepository.save(cliente);
        usuarioRepository.save(motorista);

        // Salvar a avaliação
        return avaliacaoRepository.save(avaliacao);
    }

    public List<Avaliacao> buscarAvaliacoesPorMotorista(Long motoristaId) {
        Usuario motorista = usuarioRepository.findById(motoristaId)
                .orElseThrow(() -> new EntityNotFoundException("Motorista não encontrado"));

        return avaliacaoRepository.findByMotorista(motorista);
    }

    public List<Avaliacao> buscarAvaliacoesPorCliente(Long clienteId) {
        Usuario cliente = usuarioRepository.findById(clienteId)
                .orElseThrow(() -> new EntityNotFoundException("Cliente não encontrado"));

        return avaliacaoRepository.findByCliente(cliente);
    }

    public List<Avaliacao> buscarAvaliacoesFeitas(Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado"));

        if (usuario.getRole() == Role.CLIENTE) {
            return avaliacaoRepository.findByClienteAndType(usuario, "cliente");
        } else if (usuario.getRole() == Role.MOTORISTA) {
            return avaliacaoRepository.findByMotoristaAndType(usuario, "motorista");
        }

        throw new IllegalArgumentException("Usuário não possui permissão para fazer avaliações");
    }

    public List<Avaliacao> buscarAvaliacoesRecebidas(Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado"));

        if (usuario.getRole() == Role.CLIENTE) {
            return avaliacaoRepository.findByClienteAndType(usuario, "motorista");
        } else if (usuario.getRole() == Role.MOTORISTA) {
            return avaliacaoRepository.findByMotoristaAndType(usuario, "cliente");
        }

        throw new IllegalArgumentException("Usuário não possui permissão para receber avaliações");
    }

    // Método adicional para obter apenas o total de avaliações feitas
    public Integer buscarTotalAvaliacoesFeitas(Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado"));

        return usuario.getTotalAvaliacoesFeitas();
    }

    // Método adicional para obter apenas o total de avaliações recebidas
    public Integer buscarTotalAvaliacoesRecebidas(Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado"));

        return usuario.getTotalAvaliacoesRecebidas();
    }
}