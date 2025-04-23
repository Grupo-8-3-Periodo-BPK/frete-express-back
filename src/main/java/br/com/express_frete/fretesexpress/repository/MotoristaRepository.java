package br.com.express_frete.fretesexpress.repository;

import br.com.express_frete.fretesexpress.model.Motorista;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MotoristaRepository extends JpaRepository<Motorista, Long> {
    Optional<Motorista> findByCpfCnpj(String cpfCnpj);
    Optional<Motorista> findByEmail(String email);
    Optional<Motorista> findByCnh(String cnh);
    boolean existsByCpfCnpj(String cpfCnpj);
    boolean existsByEmail(String email);
    boolean existsByCnh(String cnh);
}