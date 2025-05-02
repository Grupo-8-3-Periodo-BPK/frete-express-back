package br.com.express_frete.fretesexpress.repository;

import br.com.express_frete.fretesexpress.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    boolean existsByCpfCnpj(String cpfcnpj);
}
