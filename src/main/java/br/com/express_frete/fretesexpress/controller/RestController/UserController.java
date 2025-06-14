package br.com.express_frete.fretesexpress.controller.RestController;

import br.com.express_frete.fretesexpress.model.User;
import br.com.express_frete.fretesexpress.model.enums.Role;
import br.com.express_frete.fretesexpress.service.UserService;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    // GET: List all users
    @GetMapping
    public ResponseEntity<List<User>> findAll() {
        return ResponseEntity.ok(userService.findAll());
    }

    // GET: List all users with pagination
    @GetMapping("/paged")
    public ResponseEntity<Page<User>> findAllPaged(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sort) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(sort));
        return ResponseEntity.ok(userService.findAllPaged(pageable));
    }

    // GET: List only drivers
    @GetMapping("/drivers")
    public ResponseEntity<List<User>> findDrivers() {
        return ResponseEntity.ok(userService.findByRole(Role.DRIVER));
    }

    // GET: List only drivers with pagination
    @GetMapping("/drivers/paged")
    public ResponseEntity<Page<User>> findDriversPaged(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sort) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(sort));
        return ResponseEntity.ok(userService.findByRolePaged(Role.DRIVER, pageable));
    }

    // GET: List only clients
    @GetMapping("/clients")
    public ResponseEntity<List<User>> findClients() {
        return ResponseEntity.ok(userService.findByRole(Role.CLIENT));
    }

    // GET: List only clients with pagination
    @GetMapping("/clients/paged")
    public ResponseEntity<Page<User>> findClientsPaged(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sort) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(sort));
        return ResponseEntity.ok(userService.findByRolePaged(Role.CLIENT, pageable));
    }

    // GET: Find user by ID
    @GetMapping("/{id}")
    public ResponseEntity<User> findById(@PathVariable Long id) {
        Optional<User> userOpt = userService.findByIdOptional(id);
        return userOpt.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // POST: Create new client user
    @PostMapping("/register/client")
    public ResponseEntity<?> registerClient(@RequestBody @Valid User user) {
        try {
            user.setRole(Role.CLIENT);
            User newUser = userService.save(user);
            return ResponseEntity.status(HttpStatus.CREATED).body(newUser);
        } catch (DataIntegrityViolationException e) {
            Map<String, String> error = new HashMap<>();
            String causeMessage = e.getMostSpecificCause().getMessage();
            if (causeMessage.contains("user_username_key") || causeMessage.contains("USERNAME")) {
                error.put("error", "Nome de usuário já existe.");
            } else if (causeMessage.contains("user_email_key") || causeMessage.contains("EMAIL")) {
                error.put("error", "E-mail já cadastrado.");
            } else if (causeMessage.contains("user_cpf_cnpj_key") || causeMessage.contains("CPF_CNPJ")) {
                error.put("error", "CPF/CNPJ já cadastrado.");
            } else {
                error.put("error", "Erro de integridade de dados. Um campo único já está em uso.");
            }
            return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
        } catch (ConstraintViolationException e) {
            Map<String, String> errors = new HashMap<>();
            e.getConstraintViolations().forEach(violation -> {
                String fieldName = violation.getPropertyPath().toString();
                String message = violation.getMessage();
                errors.put(fieldName, message);
            });
            return ResponseEntity.badRequest().body(errors);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    // POST: Create new driver user
    @PostMapping("/register/driver")
    public ResponseEntity<?> registerDriver(@RequestBody @Valid User user) {
        try {
            user.setRole(Role.DRIVER);
            User newUser = userService.save(user);
            return ResponseEntity.status(HttpStatus.CREATED).body(newUser);
        } catch (DataIntegrityViolationException e) {
            Map<String, String> error = new HashMap<>();
            String causeMessage = e.getMostSpecificCause().getMessage();
            if (causeMessage.contains("user_username_key") || causeMessage.contains("USERNAME")) {
                error.put("error", "Nome de usuário já existe.");
            } else if (causeMessage.contains("user_email_key") || causeMessage.contains("EMAIL")) {
                error.put("error", "E-mail já cadastrado.");
            } else if (causeMessage.contains("user_cpf_cnpj_key") || causeMessage.contains("CPF_CNPJ")) {
                error.put("error", "CPF/CNPJ já cadastrado.");
            } else {
                error.put("error", "Erro de integridade de dados. Um campo único já está em uso.");
            }
            return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
        } catch (ConstraintViolationException e) {
            Map<String, String> errors = new HashMap<>();
            e.getConstraintViolations().forEach(violation -> {
                String fieldName = violation.getPropertyPath().toString();
                String message = violation.getMessage();
                errors.put(fieldName, message);
            });
            return ResponseEntity.badRequest().body(errors);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    // PUT: Update existing user
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody @Valid User user) {
        try {
            User updatedUser = userService.update(id, user);
            if (updatedUser != null) {
                return ResponseEntity.ok(updatedUser);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (ConstraintViolationException e) {
            Map<String, String> errors = new HashMap<>();
            e.getConstraintViolations().forEach(violation -> {
                String fieldName = violation.getPropertyPath().toString();
                String message = violation.getMessage();
                errors.put(fieldName, message);
            });
            return ResponseEntity.badRequest().body(errors);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    // DELETE: Remove user by ID
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        Optional<User> userOpt = userService.findByIdOptional(id);
        if (userOpt.isPresent()) {
            try {
                userService.delete(id);
                return ResponseEntity.noContent().build();
            } catch (DataIntegrityViolationException e) {
                Map<String, String> error = new HashMap<>();
                error.put("error",
                        "Não é possível remover o usuário. Ele está associado a outros registros no sistema (por exemplo, rastreamentos ou contratos).");
                return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
            }
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
