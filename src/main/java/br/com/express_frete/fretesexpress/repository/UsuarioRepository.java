package br.com.express_frete.fretesexpress.repository;

import br.com.express_frete.fretesexpress.model.Usuario;
import br.com.express_frete.fretesexpress.model.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByEmail(String email);
    List<Usuario> findByRole(Role role);
}