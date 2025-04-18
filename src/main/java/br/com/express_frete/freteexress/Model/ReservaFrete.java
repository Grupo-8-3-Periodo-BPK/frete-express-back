package br.com.express_frete.freteexress.Model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class ReservaFrete {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime dataReserva = LocalDateTime.now();

    @ManyToOne
    @JoinColumn(name = "frete_id")
    private Frete frete;

    @ManyToOne
    @JoinColumn(name = "caminhoneiro_id")
    private Caminhoneiro caminhoneiro;

    // Getters e Setters
}
