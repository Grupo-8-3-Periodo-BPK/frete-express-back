package br.com.express_frete.fretesexpress.repository;

import br.com.express_frete.fretesexpress.model.FormularioSuporte;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FormularioSuporteRepository extends JpaRepository<FormularioSuporte, Long> {
    Optional<FormularioSuporte> findByEmailRetorno(String emailRetorno);
}
