package br.com.express_frete.fretesexpress.dto;

import br.com.express_frete.fretesexpress.model.enums.VehicleCategory;
import br.com.express_frete.fretesexpress.model.enums.BodyType;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class VehicleDTO {
    private Long id;
    private String renavam;
    private String brand;
    private Integer year;
    private String model;

    @JsonProperty("license_plate")
    private String licensePlate;

    private String color;
    private Double weight;
    private Double length;
    private Double width;
    private Double height;

    @JsonProperty("axles_count")
    private Integer axlesCount;

    @JsonProperty("has_canvas")
    private Boolean hasCanvas;

    private VehicleCategory category;

    @JsonProperty("body_type")
    private BodyType bodyType;
}