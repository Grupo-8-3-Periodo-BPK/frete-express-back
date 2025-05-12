package br.com.express_frete.fretesexpress.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import br.com.express_frete.fretesexpress.model.enums.VehicleCategory;
import br.com.express_frete.fretesexpress.model.enums.BodyType;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "vehicle")
public class Vehicle {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Renavam number is required")
    @Pattern(regexp = "\\d{9}|\\d{11}", message = "Invalid Renavam. Must have 9 or 11 digits")
    private String renavam;

    @NotBlank(message = "Brand is required")
    @Size(max = 50, message = "Brand must have at most 50 characters")
    private String brand;

    @Min(value = 1800, message = "Year must be greater than or equal to 1800")
    @Max(value = 2100, message = "Year must be less than or equal to 2100")
    private Integer year;

    @NotBlank(message = "Model is required")
    @Size(max = 50, message = "Model must have at most 50 characters")
    private String model;

    @NotBlank(message = "License plate is required")
    @Pattern(regexp = "[A-Z]{3}[0-9][0-9A-Z][0-9]{2}|[A-Z]{3}-[0-9]{4}", message = "Invalid plate. Use Mercosul format (AAA0A00)")
    @Column(unique = true)
    private String licensePlate;

    @NotBlank(message = "Color is required")
    @Size(max = 30, message = "Color must have at most 30 characters")
    private String color;

    @PositiveOrZero(message = "Weight must be greater than or equal to 0")
    private Double weight;

    @PositiveOrZero(message = "Length must be greater than or equal to 0")
    private Double length;

    @PositiveOrZero(message = "Width must be greater than or equal to 0")
    private Double width;

    @PositiveOrZero(message = "Height must be greater than or equal to 0")
    private Double height;

    @Min(value = 1, message = "Number of axles must be at least 1")
    private Integer axlesCount;

    @NotNull(message = "Information about canvas is required")
    private Boolean hasCanvas;

    @NotNull(message = "Category is required")
    @Enumerated(EnumType.STRING)
    private VehicleCategory category;

    @NotNull(message = "Body type is required")
    @Enumerated(EnumType.STRING)
    private BodyType bodyType;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @JsonBackReference
    private User user;
}