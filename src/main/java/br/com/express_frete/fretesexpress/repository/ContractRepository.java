package br.com.express_frete.fretesexpress.repository;

import br.com.express_frete.fretesexpress.model.Contract;
import br.com.express_frete.fretesexpress.model.User;
import br.com.express_frete.fretesexpress.model.Freight;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContractRepository extends JpaRepository<Contract, Long> {
    List<Contract> findByClient(User client);

    List<Contract> findByDriver(User driver);

    List<Contract> findByFreight(Freight freight);
}