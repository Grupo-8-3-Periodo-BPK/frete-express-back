package br.com.express_frete.freteexress.Model;

import jakarta.persistence.*;
import java.util.List;

@Entity
public class Empresa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;
    private String cnpj;
    private String email;
    private String telefone;

    @OneToMany(mappedBy = "empresa")
    private List<Frete> fretes;

    // Getters e Setters
}
