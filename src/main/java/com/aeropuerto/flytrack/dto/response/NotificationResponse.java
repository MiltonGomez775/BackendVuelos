package com.aeropuerto.flytrack.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class NotificationResponse {
    private Long id;
    private String message;
    private Long flightId;
    private String flightNumber;
    private boolean read;
    private LocalDateTime createdAt;
}
