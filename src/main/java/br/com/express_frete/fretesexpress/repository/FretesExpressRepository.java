package br.com.express_frete.fretesexpress.repository;

import br.com.express_frete.fretesexpress.model.FretesExpress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FretesExpressRepository extends JpaRepository<FretesExpress, Long> {
}
