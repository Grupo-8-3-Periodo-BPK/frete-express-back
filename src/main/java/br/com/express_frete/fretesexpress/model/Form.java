package br.com.express_frete.fretesexpress.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
@Entity
@Table(name = "form")
public class Form {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "The name of the requester is required")
    @Size(max = 100, message = "The name must have a maximum of 100 characters")
    @Column(name = "name_requester")
    private String nameRequester;

    @NotBlank(message = "The return email is required")
    @Email(message = "Invalid email")
    @Size(max = 100, message = "The email must have a maximum of 100 characters")
    @Column(name = "return_email")
    private String returnEmail;

    @NotBlank(message = "The subject is required")
    @Size(max = 200, message = "The subject must have a maximum of 200 characters")
    private String subject;

    @NotBlank(message = "The problem description is required")
    @Column(name = "problem_description")
    private String problemDescription;
}