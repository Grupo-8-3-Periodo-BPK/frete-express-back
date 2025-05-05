package br.com.express_frete.fretesexpress.service;

import br.com.express_frete.fretesexpress.Validation.DriverValidation;
import br.com.express_frete.fretesexpress.model.User;
import br.com.express_frete.fretesexpress.model.enums.Role;
import br.com.express_frete.fretesexpress.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private Validator validator;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public List<User> findByRole(Role role) {
        return userRepository.findByRole(role);
    }

    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + id));
    }

    public Optional<User> findByIdOptional(Long id) {
        return userRepository.findById(id);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public Optional<User> findByCpf_cnpj(String cpf_cnpj) {
        return userRepository.findByCpf_cnpj(cpf_cnpj);
    }

    public User save(User user) {
        if (Role.DRIVER.equals(user.getRole())) {
            Set<ConstraintViolation<User>> violations = validator.validate(user, DriverValidation.class);
            if (!violations.isEmpty()) {
                throw new ConstraintViolationException(violations);
            }
        }

        // Encrypt password before saving
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }

        return userRepository.save(user);
    }

    public void delete(Long id) {
        userRepository.deleteById(id);
    }

    public User update(Long id, User updatedData) {
        return userRepository.findById(id).map(user -> {
            user.setName(updatedData.getName());
            user.setEmail(updatedData.getEmail());

            // If password is provided, encrypt and update
            if (updatedData.getPassword() != null && !updatedData.getPassword().isEmpty()) {
                user.setPassword(passwordEncoder.encode(updatedData.getPassword()));
            }

            // Update role and validate driver's license if role is DRIVER
            if (updatedData.getRole() != null) {
                user.setRole(updatedData.getRole());

                // If role is DRIVER, validate driver's license
                if (Role.DRIVER.equals(updatedData.getRole())) {
                    user.setCnh(updatedData.getCnh());

                    Set<ConstraintViolation<User>> violations = validator.validate(user,
                            DriverValidation.class);
                    if (!violations.isEmpty()) {
                        throw new ConstraintViolationException(violations);
                    }
                }
            }

            // Update other fields
            if (updatedData.getCpfCnpj() != null) {
                user.setCpfCnpj(updatedData.getCpfCnpj());
            }

            return userRepository.save(user);
        }).orElse(null);
    }

    /**
     * Checks if user credentials are valid
     * 
     * @param email    user email
     * @param password plain text password
     * @return Optional with user if credentials are valid, empty otherwise
     */
    public Optional<User> authenticate(String email, String password) {
        Optional<User> user = userRepository.findByEmail(email);

        if (user.isPresent() && passwordEncoder.matches(password, user.get().getPassword())) {
            return user;
        }

        return Optional.empty();
    }
}