package br.com.express_frete.fretesexpress.repository;

import br.com.express_frete.fretesexpress.model.Freight;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FreightRepository extends JpaRepository<Freight, Long> {
}
