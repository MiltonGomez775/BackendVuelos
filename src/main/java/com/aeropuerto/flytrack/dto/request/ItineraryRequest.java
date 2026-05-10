package com.aeropuerto.flytrack.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ItineraryRequest {

    @NotNull(message = "Flight ID is required")
    private Long flightId;

    private String seatNumber;
}
