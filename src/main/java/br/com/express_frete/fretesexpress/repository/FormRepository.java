package br.com.express_frete.fretesexpress.repository;

import br.com.express_frete.fretesexpress.model.Form;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FormRepository extends JpaRepository<Form, Long> {
    Optional<Form> findByReturnEmail(String returnEmail);
}
