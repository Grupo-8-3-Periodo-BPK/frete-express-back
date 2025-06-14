package br.com.express_frete.fretesexpress.service;

import br.com.express_frete.fretesexpress.dto.NewPasswordDTO;
import br.com.express_frete.fretesexpress.dto.RecoveryRequestDTO;
import br.com.express_frete.fretesexpress.dto.TokenValidationDTO;
import br.com.express_frete.fretesexpress.model.PasswordResetToken;
import br.com.express_frete.fretesexpress.model.RecoveryLog;
import br.com.express_frete.fretesexpress.model.User;
import br.com.express_frete.fretesexpress.repository.PasswordResetTokenRepository;
import br.com.express_frete.fretesexpress.repository.RecoveryLogRepository;
import br.com.express_frete.fretesexpress.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
public class PasswordRecoveryService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordResetTokenRepository tokenRepository;

    @Autowired
    private RecoveryLogRepository logRepository;

    @Autowired
    private EmailService emailService;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    private static final long TOKEN_VALIDITY_MINUTES = 10;

    public void requestRecovery(RecoveryRequestDTO dto) {
        Optional<User> userOpt = userRepository.findByEmail(dto.getEmailOrPhone());
        if (userOpt.isEmpty()) {
            throw new IllegalArgumentException("Usuário não encontrado com o e-mail informado");
        }

        User user = userOpt.get();

        String token = UUID.randomUUID().toString();
        Instant expiration = Instant.now().plusSeconds(TOKEN_VALIDITY_MINUTES * 60);

        PasswordResetToken tokenObj = new PasswordResetToken();
        tokenObj.setToken(token);
        tokenObj.setExpiration(expiration);
        tokenObj.setUser(user);
        tokenRepository.save(tokenObj);

        // Envia e-mail com o token
        String subject = "Recuperação de Senha - Fretes Express";
        String content = "Olá, " + user.getName() + ".\n\n" +
                "Use o código abaixo para redefinir sua senha. Ele é válido por 10 minutos:\n\n" +
                token + "\n\n" +
                "Se não foi você, ignore este e-mail.";

        emailService.sendSimpleEmail(user.getEmail(), subject, content);

        // Log - SOLICITADO
        saveLog(user.getEmail(), "SOLICITADO");

        System.out.println("[LOG] Token de recuperação gerado para o usuário " + user.getEmail());
    }

    public boolean validateToken(TokenValidationDTO dto) {
        Optional<PasswordResetToken> tokenOpt = tokenRepository.findByToken(dto.getToken());

        if (tokenOpt.isEmpty()) return false;

        PasswordResetToken token = tokenOpt.get();

        return !token.isUsed() && token.getExpiration().isAfter(Instant.now());
    }

    @Transactional
    public void resetPassword(NewPasswordDTO dto) {
        Optional<PasswordResetToken> tokenOpt = tokenRepository.findByToken(dto.getToken());
        if (tokenOpt.isEmpty()) {
            saveFailureLog("desconhecido");
            throw new IllegalArgumentException("Token inválido");
        }

        PasswordResetToken token = tokenOpt.get();

        if (token.isUsed() || token.getExpiration().isBefore(Instant.now())) {
            saveFailureLog(token.getUser().getEmail());
            throw new IllegalArgumentException("Token expirado ou já utilizado");
        }

        if (!isPasswordStrong(dto.getNewPassword())) {
            saveFailureLog(token.getUser().getEmail());
            throw new IllegalArgumentException("A senha deve conter no mínimo 8 caracteres, 1 letra maiúscula e 1 número");
        }

        User user = token.getUser();
        user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        userRepository.save(user);

        token.setUsed(true);
        tokenRepository.save(token);

        // Log RN07 - REDEFINIDO
        saveLog(user.getEmail(), "REDEFINIDO");

        System.out.println("[LOG] Senha redefinida para o usuário " + user.getEmail());
    }

    // Validação de senha segura
    private boolean isPasswordStrong(String password) {
        return password.length() >= 8 &&
                password.matches(".*[A-Z].*") &&
                password.matches(".*\\d.*");
    }

    // Log genérico para SOLICITADO / REDEFINIDO
    private void saveLog(String email, String status) {
        RecoveryLog log = new RecoveryLog();
        log.setEmail(email);
        log.setTimestamp(Instant.now());
        log.setStatus(status);
        log.setIp("127.0.0.1");
        logRepository.save(log);
    }

    // Log de tentativa com falha
    private void saveFailureLog(String email) {
        saveLog(email, "FALHA");
        System.out.println("[LOG] Tentativa falha de redefinição de senha para " + email);
    }
}
