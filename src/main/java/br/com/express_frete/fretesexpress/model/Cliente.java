package br.com.express_frete.fretesexpress.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table (name = "cliente")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long IdCliente;

    @Column
    private String Nome;

    @Column
    private String email;

    @Column
    private String telefone;

    @Column(unique = True)
    private String cpfcnpj;

    private String senha;

    public Long getIdCliente() {
        return IdCliente;
    }

    public String getNome() {
        return Nome;
    }

    public String getEmail() {
        return email;
    }

    public String getTelefone() {
        return telefone;
    }

    public String getCpfcnpj() {
        return cpfcnpj;
    }

    public String getSenha() {
        return senha;
    }

    public void setIdCliente(Long idCliente) {
        IdCliente = idCliente;
    }

    public void setNome(String nome) {
        Nome = nome;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public void setCpfcnpj(String cpfcnpj) {
        this.cpfcnpj = cpfcnpj;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }
}

