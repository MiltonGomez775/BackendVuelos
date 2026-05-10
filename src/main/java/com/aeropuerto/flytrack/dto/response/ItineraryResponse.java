package com.aeropuerto.flytrack.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ItineraryResponse {
    private Long id;
    private Long passengerId;
    private String passengerName;
    private FlightResponse flight;
    private String seatNumber;
    private String bookingCode;
    private LocalDateTime createdAt;
}
