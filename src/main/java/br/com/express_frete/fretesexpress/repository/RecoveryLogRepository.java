package br.com.express_frete.fretesexpress.repository;

import br.com.express_frete.fretesexpress.model.RecoveryLog;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface RecoveryLogRepository extends JpaRepository<RecoveryLog, Long> {
    List<RecoveryLog> findByEmail(String email);
}
