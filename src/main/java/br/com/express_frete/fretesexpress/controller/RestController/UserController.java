package br.com.express_frete.fretesexpress.controller.RestController;

import br.com.express_frete.fretesexpress.model.User;
import br.com.express_frete.fretesexpress.model.enums.Role;
import br.com.express_frete.fretesexpress.service.UserService;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public List<User> findAll() {
        return userService.findAll();
    }

    // GET: List only drivers
    @GetMapping("/drivers")
    public List<User> findDrivers() {
        return userService.findByRole(Role.DRIVER);
    }

    // GET: List only clients
    @GetMapping("/clients")
    public List<User> findClients() {
        return userService.findByRole(Role.CLIENT);
    }

    // GET: Find user by ID
    @GetMapping("/{id}")
    public ResponseEntity<User> findById(@PathVariable Long id) {
        Optional<User> userOpt = userService.findByIdOptional(id);
        return userOpt.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // POST: Create new user
    @PostMapping
    public ResponseEntity<?> create(@RequestBody @Valid User user) {
        try {
            User newUser = userService.save(user);
            return ResponseEntity.status(HttpStatus.CREATED).body(newUser);
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
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        Optional<User> userOpt = userService.findByIdOptional(id);
        if (userOpt.isPresent()) {
            userService.delete(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
