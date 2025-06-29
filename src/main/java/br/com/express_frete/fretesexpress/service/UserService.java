package br.com.express_frete.fretesexpress.service;

import br.com.express_frete.fretesexpress.Validation.DriverValidation;
import br.com.express_frete.fretesexpress.dto.UserUpdateDTO;
import br.com.express_frete.fretesexpress.model.User;
import br.com.express_frete.fretesexpress.model.enums.Role;
import br.com.express_frete.fretesexpress.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    // Método para buscar todos os usuários com paginação
    public Page<User> findAllPaged(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    public List<User> findByRole(Role role) {
        return userRepository.findByRole(role);
    }

    // Método para buscar usuários por papel com paginação
    public Page<User> findByRolePaged(Role role, Pageable pageable) {
        return userRepository.findByRole(role, pageable);
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

    public User update(Long id, UserUpdateDTO updatedData) {
        return userRepository.findById(id).map(user -> {
            if (updatedData.getName() != null && !updatedData.getName().isEmpty()) {
                user.setName(updatedData.getName());
            }

            if (updatedData.getEmail() != null && !updatedData.getEmail().isEmpty()) {
                user.setEmail(updatedData.getEmail());
            }

            if (updatedData.getPhone() != null) {
                user.setPhone(updatedData.getPhone());
            }

            if (updatedData.getPassword() != null && !updatedData.getPassword().isEmpty()) {
                user.setPassword(passwordEncoder.encode(updatedData.getPassword()));
            }

            return userRepository.save(user);
        }).orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + id));
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

                    Set<ConstraintViolation<User>> violations = validator.validate(user, DriverValidation.class);
                    if (!violations.isEmpty()) {
                        throw new ConstraintViolationException(violations);
                    }
                }
            }

            // Update other fields
            if (updatedData.getCpfCnpj() != null) {
                user.setCpfCnpj(updatedData.getCpfCnpj());
            }
            // Add phone update
            if (updatedData.getPhone() != null) {
                user.setPhone(updatedData.getPhone());
            }

            // Explicitly return the saved user
            return userRepository.save(user);
        }).orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + id));
    }

}