package br.com.express_frete.fretesexpress.service;

import br.com.express_frete.fretesexpress.dto.LoginRequestDTO;
import br.com.express_frete.fretesexpress.dto.LoginResponseDTO;
import br.com.express_frete.fretesexpress.model.Usuario;
import br.com.express_frete.fretesexpress.model.enums.Role;
import br.com.express_frete.fretesexpress.repository.UsuarioRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;

@Service
public class AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);
    private static final int MAX_TENTATIVAS = 5;

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration.ms}")
    private long jwtExpirationMs;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public LoginResponseDTO login(LoginRequestDTO loginRequest) {
        logger.info("Tentativa de login para e-mail: {}", loginRequest.getEmail());

        Usuario usuario = usuarioRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> {
                    logger.warn("Usuário com e-mail {} não encontrado", loginRequest.getEmail());
                    return new BadCredentialsException("Credenciais inválidas");
                });

        if (usuario.isBloqueado()) {
            logger.warn("Usuário {} está bloqueado até {}", loginRequest.getEmail(),
                    usuario.getDataBloqueio() != null ? usuario.getDataBloqueio().plusMinutes(15) : "indefinido");
            throw new IllegalStateException("Conta bloqueada temporariamente. Tente novamente mais tarde.");
        }

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getSenha()));
            logger.info("Login bem-sucedido para e-mail: {}", loginRequest.getEmail());

            usuario.resetarTentativasLogin();
            try {
                usuarioRepository.save(usuario);
                logger.info("Tentativas de login resetadas para usuário: {}", loginRequest.getEmail());
            } catch (DataIntegrityViolationException e) {
                logger.error("Erro ao salvar usuário após login bem-sucedido: {}", e.getMessage(), e);
                throw new RuntimeException("Erro ao atualizar tentativas de login", e);
            }

            SecretKey key = Keys.hmacShaKeyFor(Base64.getDecoder().decode(jwtSecret));
            String jwt = Jwts.builder()
                    .setSubject(loginRequest.getEmail())
                    .setIssuedAt(new Date())
                    .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
                    .signWith(key)
                    .compact();

            LoginResponseDTO response = new LoginResponseDTO();
            response.setToken(jwt);
            return response;

        } catch (BadCredentialsException e) {
            try {
                // Garantir que tentativasLogin não seja null
                if (usuario.getTentativasLogin() == null) {
                    usuario.setTentativasLogin(0);
                    logger.warn("tentativasLogin estava null para usuário {}, inicializado como 0", loginRequest.getEmail());
                }
                usuario.incrementarTentativasLogin();
                int tentativasAtuais = usuario.getTentativasLogin();
                logger.info("Tentativa de login malsucedida para {}. Tentativas atuais: {}", loginRequest.getEmail(), tentativasAtuais);

                if (tentativasAtuais >= MAX_TENTATIVAS) {
                    usuario.bloquear();
                    logger.warn("Usuário {} bloqueado após {} tentativas malsucedidas", loginRequest.getEmail(), MAX_TENTATIVAS);
                    usuarioRepository.save(usuario);
                    throw new IllegalStateException("Conta bloqueada temporariamente. Tente novamente mais tarde.");
                }

                usuarioRepository.save(usuario);
                logger.info("Tentativas de login atualizadas para {}: {}", loginRequest.getEmail(), tentativasAtuais);
                throw new BadCredentialsException("Credenciais inválidas");
            } catch (DataIntegrityViolationException ex) {
                logger.error("Erro ao salvar usuário após tentativa de login malsucedida: {}", ex.getMessage(), ex);
                throw new BadCredentialsException("Credenciais inválidas", ex);
            } catch (Exception ex) {
                logger.error("Erro inesperado ao processar tentativa de login para {}: {}", loginRequest.getEmail(), ex.getMessage(), ex);
                throw new BadCredentialsException("Credenciais inválidas", ex);
            }
        }
    }

    public void registrarUsuario(String email, String senha, String role) {
        try {
            if (!senha.matches("^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,}$")) {
                logger.warn("Senha inválida para e-mail: {}", email);
                throw new IllegalArgumentException("A senha deve ter no mínimo 8 caracteres, incluindo letras, números e símbolos");
            }
            if (usuarioRepository.findByEmail(email).isPresent()) {
                logger.warn("E-mail já cadastrado: {}", email);
                throw new IllegalArgumentException("E-mail já cadastrado");
            }
            Role userRole;
            try {
                userRole = Role.valueOf(role.toUpperCase());
            } catch (IllegalArgumentException e) {
                logger.warn("Papel inválido: {}", role);
                throw new IllegalArgumentException("Papel inválido: " + role);
            }
            Usuario usuario = new Usuario();
            usuario.setEmail(email);
            usuario.setSenha(passwordEncoder.encode(senha));
            usuario.setRole(userRole);
            usuario.setNome("Usuário Padrão");
            usuario.setTentativasLogin(0);
            usuarioRepository.save(usuario);
            logger.info("Usuário registrado com e-mail: {}", email);
        } catch (Exception e) {
            logger.error("Erro ao registrar usuário com e-mail: {}. Erro: {}", email, e.getMessage());
            throw e;
        }
    }
}