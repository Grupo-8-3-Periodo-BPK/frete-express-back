package br.com.express_frete.fretesexpress.repository;

import br.com.express_frete.fretesexpress.model.Veiculo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VeiculoRepository extends JpaRepository<Veiculo, Long> {
    Optional<Veiculo> findByPlaca(String placa);
    boolean existsByPlaca(String placa);
    Optional<Veiculo> findByRenavam(String renavam);
    boolean existsByRenavam(String renavam);
}