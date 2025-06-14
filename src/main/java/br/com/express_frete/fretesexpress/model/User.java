package br.com.express_frete.fretesexpress.model;

import br.com.express_frete.fretesexpress.Validation.DriverValidation;
import br.com.express_frete.fretesexpress.model.enums.Role;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.util.List;

@Entity
@Table(name = "user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(unique = true)
    private String username;

    @NotBlank
    private String name;

    @Email(message = "Invalid email")
    @NotBlank
    @Size(max = 100, message = "The email must be at most 100 characters")
    @Column(unique = true)
    private String email;

    @NotBlank
    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;

    @NotNull(groups = DriverValidation.class)
    @NotBlank(groups = DriverValidation.class, message = "The CNH is required")
    private String cnh;

    @Column(name = "cpf_cnpj", unique = true)
    @JsonProperty("cpf_cnpj")
    private String cpf_cnpj;

    @Column(name = "phone")
    private String phone;

    @Column(name = "total_reviews_received")
    private Integer totalReviewsReceived = 0;

    @Column(name = "total_reviews_made")
    private Integer totalReviewsMade = 0;

    // Internal relationships that are not serialized
    @OneToMany(mappedBy = "driver")
    @JsonIgnore
    private List<Review> receivedReviews;

    @OneToMany(mappedBy = "client")
    @JsonIgnore
    private List<Review> madeReviews;

    // Vehicle relationship (for drivers)
    @OneToMany(mappedBy = "user")
    @JsonIgnore
    private List<Vehicle> vehicles;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    @JsonIgnore
    public List<Review> getReceivedReviews() {
        return receivedReviews;
    }

    public void setReceivedReviews(List<Review> receivedReviews) {
        this.receivedReviews = receivedReviews;
    }

    @JsonIgnore
    public List<Review> getMadeReviews() {
        return madeReviews;
    }

    public void setMadeReviews(List<Review> madeReviews) {
        this.madeReviews = madeReviews;
    }

    public Integer getTotalReviewsReceived() {
        return totalReviewsReceived;
    }

    public void setTotalReviewsReceived(Integer totalReviewsReceived) {
        this.totalReviewsReceived = totalReviewsReceived;
    }

    public Integer getTotalReviewsMade() {
        return totalReviewsMade;
    }

    public void setTotalReviewsMade(Integer totalReviewsMade) {
        this.totalReviewsMade = totalReviewsMade;
    }

    public void incrementReviewsMade() {
        this.totalReviewsMade++;
    }

    public void incrementReviewsReceived() {
        this.totalReviewsReceived++;
    }

    public String getCnh() {
        return cnh;
    }

    public void setCnh(String cnh) {
        this.cnh = cnh;
    }

    @JsonProperty("cpf_cnpj")
    public String getCpfCnpj() {
        return cpf_cnpj;
    }

    @JsonProperty("cpf_cnpj")
    public void setCpfCnpj(String cpf_cnpj) {
        this.cpf_cnpj = cpf_cnpj;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    @JsonIgnore
    public List<Vehicle> getVehicles() {
        return vehicles;
    }

    public void setVehicles(List<Vehicle> vehicles) {
        this.vehicles = vehicles;
    }
}