package br.com.express_frete.fretesexpress.controller.RestController;

import br.com.express_frete.fretesexpress.model.User;
import br.com.express_frete.fretesexpress.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody @Valid User user) {
        try {
            // O papel (role) é recebido diretamente do corpo da requisição
            User newUser = userService.save(user);
            return ResponseEntity.status(HttpStatus.CREATED).body(newUser);
        } catch (DataIntegrityViolationException e) {
            Map<String, String> error = new HashMap<>();
            String causeMessage = e.getMostSpecificCause().getMessage();
            if (causeMessage.contains("user_username_key")) {
                error.put("error", "Nome de usuário já existe.");
            } else if (causeMessage.contains("user_email_key")) {
                error.put("error", "E-mail já cadastrado.");
            } else if (causeMessage.contains("user_cpf_cnpj_key")) {
                error.put("error", "CPF/CNPJ já cadastrado.");
            } else {
                error.put("error", "Erro de integridade de dados.");
            }
            return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
}