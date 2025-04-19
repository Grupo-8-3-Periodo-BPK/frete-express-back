package br.com.express_frete.fretesexpress.repository;

import br.com.express_frete.fretesexpress.model.Avaliacao;
import br.com.express_frete.fretesexpress.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AvaliacaoRepository extends JpaRepository<Avaliacao, Long> {
    List<Avaliacao> findByMotorista(Usuario motorista);
    List<Avaliacao> findByCliente(Usuario cliente);
    List<Avaliacao> findByClienteAndType(Usuario cliente, String type);
    List<Avaliacao> findByMotoristaAndType(Usuario motorista, String type);
}