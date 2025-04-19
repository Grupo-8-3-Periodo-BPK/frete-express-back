package br.com.express_frete.fretesexpress.repository;

import br.com.express_frete.fretesexpress.model.Frete;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FreteRepository extends JpaRepository<Frete, Long> {
}
