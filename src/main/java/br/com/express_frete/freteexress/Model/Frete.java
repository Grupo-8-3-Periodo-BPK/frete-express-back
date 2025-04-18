package br.com.express_frete.freteexress.Model;

import jakarta.persistence.*;

@Entity
public class Frete {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String origem;
    private String destino;
    private double valor;
    private double peso;
    private String descricao;
    private String status = "Disponível";

    // dentro da classe Frete
    @ManyToOne
    @JoinColumn(name = "empresa_id")
    private Empresa empresa;


    @OneToOne(mappedBy = "frete")
    private ReservaFrete reserva;

    // Getters e Setters
}
