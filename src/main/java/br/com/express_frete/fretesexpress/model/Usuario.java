package br.com.express_frete.fretesexpress.model;

import br.com.express_frete.fretesexpress.model.enums.Role;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "usuarios")
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String nome;

    @Email
    @NotBlank
    private String email;

    @NotBlank
    private String senha;

    @Enumerated(EnumType.STRING)
    private Role role;

    private Integer totalAvaliacoesRecebidas = 0;
    private Integer totalAvaliacoesFeitas = 0;

    // Relacionamentos mantidos para uso interno, mas marcados para não serem serializados
    @OneToMany(mappedBy = "motorista")
    @JsonIgnore
    private List<Avaliacao> avaliacoesRecebidas;

    @OneToMany(mappedBy = "cliente")
    @JsonIgnore
    private List<Avaliacao> avaliacoesFeitas;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }

    @JsonIgnore
    public List<Avaliacao> getAvaliacoesRecebidas() { return avaliacoesRecebidas; }
    public void setAvaliacoesRecebidas(List<Avaliacao> avaliacoesRecebidas) { this.avaliacoesRecebidas = avaliacoesRecebidas; }

    @JsonIgnore
    public List<Avaliacao> getAvaliacoesFeitas() { return avaliacoesFeitas; }
    public void setAvaliacoesFeitas(List<Avaliacao> avaliacoesFeitas) { this.avaliacoesFeitas = avaliacoesFeitas; }

    public Integer getTotalAvaliacoesRecebidas() {
        return totalAvaliacoesRecebidas;
    }

    public void setTotalAvaliacoesRecebidas(Integer totalAvaliacoesRecebidas) {
        this.totalAvaliacoesRecebidas = totalAvaliacoesRecebidas;
    }

    public Integer getTotalAvaliacoesFeitas() {
        return totalAvaliacoesFeitas;
    }

    public void setTotalAvaliacoesFeitas(Integer totalAvaliacoesFeitas) {
        this.totalAvaliacoesFeitas = totalAvaliacoesFeitas;
    }

    public void incrementarAvaliacoesFeitas() {
        this.totalAvaliacoesFeitas++;
    }

    public void incrementarAvaliacoesRecebidas() {
        this.totalAvaliacoesRecebidas++;
    }
}