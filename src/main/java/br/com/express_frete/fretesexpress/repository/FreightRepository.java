package br.com.express_frete.fretesexpress.repository;

import br.com.express_frete.fretesexpress.model.Freight;
import br.com.express_frete.fretesexpress.model.enums.FreightStatus;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FreightRepository extends JpaRepository<Freight, Long> {
    List<Freight> findByStatus(FreightStatus status);
}
