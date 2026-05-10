package com.aeropuerto.flytrack.controller;

import com.aeropuerto.flytrack.dto.request.ItineraryRequest;
import com.aeropuerto.flytrack.dto.response.ItineraryResponse;
import com.aeropuerto.flytrack.service.ItineraryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/itineraries")
@RequiredArgsConstructor
public class ItineraryController {

    private final ItineraryService itineraryService;

    @PostMapping
    @PreAuthorize("hasRole('PASSENGER')")
    public ResponseEntity<ItineraryResponse> createItinerary(
            @Valid @RequestBody ItineraryRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(itineraryService.createItinerary(request, userDetails.getUsername()));
    }

    @GetMapping("/my")
    @PreAuthorize("hasRole('PASSENGER')")
    public ResponseEntity<List<ItineraryResponse>> getMyItineraries(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(itineraryService.getMyItineraries(userDetails.getUsername()));
    }

    @GetMapping("/flight/{flightId}")
    @PreAuthorize("hasRole('OPERATOR') or hasRole('ADMIN')")
    public ResponseEntity<List<ItineraryResponse>> getItinerariesByFlight(@PathVariable Long flightId) {
        return ResponseEntity.ok(itineraryService.getItinerariesByFlight(flightId));
    }
}
