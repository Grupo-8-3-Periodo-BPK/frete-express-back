package br.com.express_frete.fretesexpress.repository;

import br.com.express_frete.fretesexpress.model.User;
import br.com.express_frete.fretesexpress.model.enums.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    @Query("SELECT u FROM User u WHERE u.cpf_cnpj = :cpf_cnpj")
    Optional<User> findByCpf_cnpj(@Param("cpf_cnpj") String cpf_cnpj);

    Optional<User> findByUsername(String username);

    List<User> findByRole(Role role);

    // Método com suporte a paginação
    Page<User> findAll(Pageable pageable);

    // Método de busca por papel com suporte a paginação
    Page<User> findByRole(Role role, Pageable pageable);
}