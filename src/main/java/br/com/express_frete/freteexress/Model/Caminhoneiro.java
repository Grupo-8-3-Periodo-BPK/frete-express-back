package br.com.express_frete.freteexress.Model;

import jakarta.persistence.*;
import java.util.List;

@Entity
public class Caminhoneiro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;
    private String email;
    private String telefone;
    private String senha;
    private String localizacao;

    @OneToMany(mappedBy = "caminhoneiro", cascade = CascadeType.ALL)
    private List<ReservaFrete> reservas;

    // Getters e Setters
}
