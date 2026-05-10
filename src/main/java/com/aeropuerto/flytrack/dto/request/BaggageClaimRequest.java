package com.aeropuerto.flytrack.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class BaggageClaimRequest {

    @NotNull(message = "Flight ID is required")
    private Long flightId;

    @NotBlank(message = "Description is required")
    private String description;
}
