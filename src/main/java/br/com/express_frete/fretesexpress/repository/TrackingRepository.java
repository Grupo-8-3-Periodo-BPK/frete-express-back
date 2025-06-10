package br.com.express_frete.fretesexpress.repository;

import br.com.express_frete.fretesexpress.model.Contract;
import br.com.express_frete.fretesexpress.model.Freight;
import br.com.express_frete.fretesexpress.model.Tracking;
import br.com.express_frete.fretesexpress.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TrackingRepository extends JpaRepository<Tracking, Long> {
    List<Tracking> findByFreight(Freight freight);

    List<Tracking> findByContract(Contract contract);

    List<Tracking> findByContractUser(User user);

    // Buscar o último registro de tracking para um contrato específico
    Tracking findTopByContractOrderByIdDesc(Contract contract);
}