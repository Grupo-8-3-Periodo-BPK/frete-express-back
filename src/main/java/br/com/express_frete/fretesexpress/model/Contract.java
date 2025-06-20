package br.com.express_frete.fretesexpress.model;

import br.com.express_frete.fretesexpress.model.enums.Status;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Entity
@Table(name = "contract")
public class Contract {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "client_id", nullable = false)
    private User client;

    @ManyToOne
    @JoinColumn(name = "driver_id", nullable = false)
    private User driver;

    @ManyToOne
    @JoinColumn(name = "freight_id", nullable = false)
    private Freight freight;

    @ManyToOne
    @JoinColumn(name = "vehicle_id", nullable = false)
    private Vehicle vehicle;

    @NotNull
    @Column(name = "pickup_date")
    private LocalDate pickupDate;

    @NotNull
    @Column(name = "delivery_date")
    private LocalDate deliveryDate;

    @NotNull
    @Column(name = "agreed_value")
    private Double agreedValue;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private Status status;

    // Método para gerar nome descritivo do contrato
    @Transient
    public String getDisplayName() {
        if (freight == null)
            return "Novo Contrato";

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String pickupDateStr = pickupDate != null ? pickupDate.format(formatter) : "Data não definida";

        return String.format("Frete: %s → %s (%s)",
                freight.getOrigin_city(),
                freight.getDestination_city(),
                pickupDateStr);
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Vehicle getVehicle() {
        return vehicle;
    }

    public void setVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
    }

    public LocalDate getPickupDate() {
        return pickupDate;
    }

    public void setPickupDate(LocalDate pickupDate) {
        this.pickupDate = pickupDate;
    }

    public LocalDate getDeliveryDate() {
        return deliveryDate;
    }

    public void setDeliveryDate(LocalDate deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    public Double getAgreedValue() {
        return agreedValue;
    }

    public void setAgreedValue(Double agreedValue) {
        this.agreedValue = agreedValue;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}