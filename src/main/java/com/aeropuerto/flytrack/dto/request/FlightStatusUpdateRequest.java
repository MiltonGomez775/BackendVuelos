package com.aeropuerto.flytrack.dto.request;

import com.aeropuerto.flytrack.enums.FlightStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class FlightStatusUpdateRequest {

    @NotNull(message = "Status is required")
    private FlightStatus status;

    private String gate;
}
