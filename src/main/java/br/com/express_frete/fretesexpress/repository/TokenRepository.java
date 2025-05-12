package br.com.express_frete.fretesexpress.repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import br.com.express_frete.fretesexpress.model.Token;
import jakarta.transaction.Transactional;

@Repository
public interface TokenRepository extends JpaRepository<Token, Long> {

    /**
     * Encontra um token pelo seu valor
     */
    Optional<Token> findByToken(String token);

    /**
     * Encontra tokens pelo ID do usuário
     */
    @Query("SELECT t FROM Token t WHERE t.user_id = :userId")
    List<Token> findByUserId(@Param("userId") Long userId);

    /**
     * Encontra todos os tokens com data de expiração anterior à data fornecida
     */
    @Query("SELECT t FROM Token t WHERE t.expiration < :expiration")
    List<Token> findAllByExpirationBefore(Instant expiration);

    /**
     * Invalida o token de um usuário específico
     */
    @Transactional
    @Modifying
    @Query("UPDATE Token t SET t.valid = false WHERE t.user_id = :user_id")
    void invalidateUserToken(Long user_id);

    /**
     * Invalida todos os tokens expirados
     */
    @Transactional
    @Modifying
    @Query("UPDATE Token t SET t.valid = false WHERE t.expiration < :now")
    void invalidateAllExpiredTokens(Instant now);

}
