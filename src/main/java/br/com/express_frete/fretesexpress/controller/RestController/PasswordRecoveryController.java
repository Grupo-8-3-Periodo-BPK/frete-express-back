package br.com.express_frete.fretesexpress.controller.RestController;

import br.com.express_frete.fretesexpress.dto.NewPasswordDTO;
import br.com.express_frete.fretesexpress.dto.RecoveryRequestDTO;
import br.com.express_frete.fretesexpress.dto.TokenValidationDTO;
import br.com.express_frete.fretesexpress.service.PasswordRecoveryService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/recovery")
public class PasswordRecoveryController {

    @Autowired
    private PasswordRecoveryService recoveryService;

    @PostMapping("/request")
    public ResponseEntity<?> requestRecovery(@Valid @RequestBody RecoveryRequestDTO dto) {
        try {
            recoveryService.requestRecovery(dto);
            return ResponseEntity.ok("Token de recuperação enviado com sucesso.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Erro ao processar recuperação: " + e.getMessage());
        }
    }

    @PostMapping("/validate")
    public ResponseEntity<?> validateToken(@Valid @RequestBody TokenValidationDTO dto) {
        boolean valid = recoveryService.validateToken(dto);
        if (valid) {
            return ResponseEntity.ok("Token válido");
        } else {
            return ResponseEntity.badRequest().body("Token inválido ou expirado");
        }
    }

    @PostMapping("/reset")
    public ResponseEntity<?> resetPassword(@Valid @RequestBody NewPasswordDTO dto) {
        try {
            recoveryService.resetPassword(dto);
            return ResponseEntity.ok("Senha redefinida com sucesso.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Erro ao redefinir senha: " + e.getMessage());
        }
    }
}
