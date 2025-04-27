package br.com.express_frete.fretesexpress.repository;

import br.com.express_frete.fretesexpress.model.OrcamentoFrete;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrcamentoFreteRepository extends JpaRepository<OrcamentoFrete, Long> {
}