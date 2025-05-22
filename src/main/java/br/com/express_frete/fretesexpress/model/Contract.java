package br.com.express_frete.fretesexpress.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
@Table(name = "contract")
public class Contract {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 45)
    private String name;

    @ManyToOne
    @JoinColumn(name = "client_id", nullable = false)
    private User client;

    @ManyToOne
    @JoinColumn(name = "driver_id", nullable = false)
    private User driver;

    @ManyToOne
    @JoinColumn(name = "freight_id", nullable = false)
    private Freight freight;

    @Column(name = "driver_accepted")
    private Boolean driverAccepted = false;

    @Column(name = "client_accepted")
    private Boolean clientAccepted = false;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public User getClient() {
        return client;
    }

    public void setClient(User client) {
        this.client = client;
    }

    public User getDriver() {
        return driver;
    }

    public void setDriver(User driver) {
        this.driver = driver;
    }

    public Freight getFreight() {
        return freight;
    }

    public void setFreight(Freight freight) {
        this.freight = freight;
    }

    public Boolean getDriverAccepted() {
        return driverAccepted;
    }

    public void setDriverAccepted(Boolean driverAccepted) {
        this.driverAccepted = driverAccepted;
    }

    public Boolean getClientAccepted() {
        return clientAccepted;
    }

    public void setClientAccepted(Boolean clientAccepted) {
        this.clientAccepted = clientAccepted;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
} 