package com.aeropuerto.flytrack.controller;

import com.aeropuerto.flytrack.dto.request.FlightRequest;
import com.aeropuerto.flytrack.dto.request.FlightStatusUpdateRequest;
import com.aeropuerto.flytrack.dto.response.FlightResponse;
import com.aeropuerto.flytrack.dto.response.MessageResponse;
import com.aeropuerto.flytrack.service.FlightService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/flights")
@RequiredArgsConstructor
public class FlightController {

    private final FlightService flightService;

    @PostMapping
    @PreAuthorize("hasRole('OPERATOR') or hasRole('ADMIN')")
    public ResponseEntity<FlightResponse> createFlight(@Valid @RequestBody FlightRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(flightService.createFlight(request));
    }

    @GetMapping
    public ResponseEntity<List<FlightResponse>> getAllFlights() {
        return ResponseEntity.ok(flightService.getAllFlights());
    }

    @GetMapping("/{id}")
    public ResponseEntity<FlightResponse> getFlightById(@PathVariable Long id) {
        return ResponseEntity.ok(flightService.getFlightById(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('OPERATOR') or hasRole('ADMIN')")
    public ResponseEntity<FlightResponse> updateFlight(@PathVariable Long id,
                                                        @Valid @RequestBody FlightRequest request) {
        return ResponseEntity.ok(flightService.updateFlight(id, request));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('OPERATOR') or hasRole('ADMIN')")
    public ResponseEntity<FlightResponse> updateStatus(@PathVariable Long id,
                                                        @Valid @RequestBody FlightStatusUpdateRequest request) {
        return ResponseEntity.ok(flightService.updateFlightStatus(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MessageResponse> deleteFlight(@PathVariable Long id) {
        flightService.deleteFlight(id);
        return ResponseEntity.ok(new MessageResponse("Flight deleted successfully"));
    }
}
