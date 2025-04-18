package br.com.express_frete.freteexress.Model;

import jakarta.persistence.*;

@Entity
public class Veiculo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String placa;
    private String modelo;
    private String tipo; // Ex: Caminhão baú, carreta, etc.
    private double capacidade;

    @ManyToOne
    @JoinColumn(name = "caminhoneiro_id")
    private Caminhoneiro caminhoneiro;

    // Getters e Setters
}
