package br.com.express_frete.fretesexpress.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
@Table(name = "tracking")
public class Tracking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "freight_id", nullable = false)
    private Freight freight;

    @Size(max = 255)
    @Column(name = "current_location")
    private String currentLocation;

    @ManyToOne
    @JoinColumn(name = "contract_id", nullable = false)
    private Contract contract;

    @ManyToOne
    @JoinColumn(name = "contract_user_id")
    private User contractUser;

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Freight getFreight() {
        return freight;
    }

    public void setFreight(Freight freight) {
        this.freight = freight;
    }

    public String getCurrentLocation() {
        return currentLocation;
    }

    public void setCurrentLocation(String currentLocation) {
        this.currentLocation = currentLocation;
    }

    public Contract getContract() {
        return contract;
    }

    public void setContract(Contract contract) {
        this.contract = contract;
    }

    public User getContractUser() {
        return contractUser;
    }

    public void setContractUser(User contractUser) {
        this.contractUser = contractUser;
    }
}