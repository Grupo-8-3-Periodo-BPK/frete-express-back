package br.com.express_frete.fretesexpress.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class FormDTO {

    private Long id;

    @NotBlank(message = "The name of the requester is required")
    @Size(max = 100, message = "The name must have a maximum of 100 characters")
    @JsonProperty("name_requester")
    private String nameRequester;

    @NotBlank(message = "The return email is required")
    @Email(message = "Invalid email")
    @Size(max = 100, message = "The email must have a maximum of 100 characters")
    @JsonProperty("return_email")
    private String returnEmail;

    @NotBlank(message = "The subject is required")
    @Size(max = 200, message = "The subject must have a maximum of 200 characters")
    private String subject;

    @NotBlank(message = "The problem description is required")
    @JsonProperty("problem_description")
    private String problemDescription;

    @JsonProperty("sending_date")
    private LocalDateTime sendingDate;
}