package br.com.express_frete.fretesexpress.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * DTO for authentication request
 */
@Data
public class AuthRequest {

    @NotBlank(message = "Login is required")
    private String login;

    @NotBlank(message = "Password is required")
    private String password;
}