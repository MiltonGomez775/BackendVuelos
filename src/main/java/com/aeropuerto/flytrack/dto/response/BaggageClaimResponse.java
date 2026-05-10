package com.aeropuerto.flytrack.dto.response;

import com.aeropuerto.flytrack.enums.ClaimStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class BaggageClaimResponse {
    private Long id;
    private Long passengerId;
    private String passengerName;
    private Long flightId;
    private String flightNumber;
    private String description;
    private ClaimStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
