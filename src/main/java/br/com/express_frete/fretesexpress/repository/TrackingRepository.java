package br.com.express_frete.fretesexpress.repository;

import br.com.express_frete.fretesexpress.model.Contract;
import br.com.express_frete.fretesexpress.model.Tracking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TrackingRepository extends JpaRepository<Tracking, Long> {
    Optional<Tracking> findByContract(Contract contract);
}