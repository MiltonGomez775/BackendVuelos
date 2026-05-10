package com.aeropuerto.flytrack.dto.response;

import com.aeropuerto.flytrack.enums.FlightStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class FlightResponse {
    private Long id;
    private String flightNumber;
    private String airline;
    private String origin;
    private String destination;
    private LocalDateTime departureTime;
    private LocalDateTime arrivalTime;
    private FlightStatus status;
    private String gate;
    private Integer availableSeats;
    private LocalDateTime createdAt;
}
