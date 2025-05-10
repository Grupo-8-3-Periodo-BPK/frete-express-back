package br.com.express_frete.fretesexpress.service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.scheduling.annotation.Scheduled;

import br.com.express_frete.fretesexpress.model.Token;
import br.com.express_frete.fretesexpress.model.User;
import br.com.express_frete.fretesexpress.repository.TokenRepository;
import jakarta.transaction.Transactional;

@Service
public class TokenService {

    @Autowired
    private TokenRepository tokenRepository;

    @Autowired
    private JwtService jwtService;

    /**
     * Cria ou atualiza um token para o usuário.
     * Certifica-se de que existe apenas um token por usuário.
     */
    @Transactional
    public Token createToken(User user) {
        // Gerar o token JWT
        String tokenString = jwtService.generateToken(user);

        // Definir a data de expiração (24 horas)
        Instant expiration = Instant.now().plus(24, ChronoUnit.HOURS);

        // Buscar tokens existentes deste usuário
        List<Token> existingTokens = tokenRepository.findByUserId(user.getId());

        // Se há tokens existentes, remova todos do banco de dados,
        // exceto um que será atualizado
        if (!existingTokens.isEmpty()) {
            // Manter apenas o primeiro token e remover os demais
            Token tokenToUpdate = existingTokens.get(0);

            // Remover tokens duplicados, se houver
            if (existingTokens.size() > 1) {
                for (int i = 1; i < existingTokens.size(); i++) {
                    tokenRepository.delete(existingTokens.get(i));
                }
            }

            // Atualizar o token mantido
            tokenToUpdate.setToken(tokenString);
            tokenToUpdate.setExpiration(expiration);
            tokenToUpdate.setValid(true);
            return tokenRepository.save(tokenToUpdate);
        } else {
            // Se não existe, criamos um novo
            Token newToken = new Token();
            newToken.setUserId(user.getId());
            newToken.setToken(tokenString);
            newToken.setExpiration(expiration);
            newToken.setValid(true);
            return tokenRepository.save(newToken);
        }
    }

    /**
     * Verifica se um token existe no banco e é válido.
     */
    public boolean isTokenValid(String token) {
        // Buscar o token no banco
        Optional<Token> tokenOptional = tokenRepository.findByToken(token);

        if (tokenOptional.isEmpty()) {
            return false;
        }

        Token tokenEntity = tokenOptional.get();

        // Verificar se o token é válido e não expirou
        if (!tokenEntity.isValid() || tokenEntity.getExpiration().isBefore(Instant.now())) {
            // Se expirou, marcamos como inválido
            tokenEntity.setValid(false);
            tokenRepository.save(tokenEntity);
            return false;
        }

        return true;
    }

    /**
     * Invalida o token de um usuário específico.
     */
    public void invalidateUserToken(Long userId) {
        // Usar o método direto do repositório para melhor performance
        tokenRepository.invalidateUserToken(userId);
    }

    /**
     * Invalida um token específico pelo seu valor.
     */
    public void invalidateToken(String token) {
        // Remover o prefixo "Bearer " se presente
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        // Buscar pelo valor do token e invalidar se encontrado
        if (token != null) {
            tokenRepository.findByToken(token).ifPresent(t -> {
                t.setValid(false);
                tokenRepository.save(t);
            });
        }
    }

    /**
     * Obtém o ID do usuário associado a um token.
     */
    public Optional<Long> getUserIdFromToken(String token) {
        // Buscar o token pelo valor
        Optional<Token> tokenOpt = tokenRepository.findByToken(token);
        return tokenOpt.map(Token::getUserId);
    }

    /**
     * Método agendado para limpar tokens expirados (definindo-os como inválidos).
     * Executa diariamente à meia-noite.
     */
    @Scheduled(cron = "0 0 0 * * *")
    public void cleanupExpiredTokens() {
        // Usar método otimizado do repositório para invalidar tokens expirados
        tokenRepository.invalidateAllExpiredTokens(Instant.now());
        System.out.println("Tokens expirados invalidados com sucesso: " + Instant.now());
    }
}