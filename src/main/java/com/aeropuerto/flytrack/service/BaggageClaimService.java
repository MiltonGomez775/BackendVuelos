package com.aeropuerto.flytrack.service;

import com.aeropuerto.flytrack.dto.request.BaggageClaimRequest;
import com.aeropuerto.flytrack.dto.request.ClaimStatusUpdateRequest;
import com.aeropuerto.flytrack.dto.response.BaggageClaimResponse;
import com.aeropuerto.flytrack.entity.BaggageClaim;
import com.aeropuerto.flytrack.entity.Flight;
import com.aeropuerto.flytrack.entity.User;
import com.aeropuerto.flytrack.exception.ResourceNotFoundException;
import com.aeropuerto.flytrack.repository.BaggageClaimRepository;
import com.aeropuerto.flytrack.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BaggageClaimService {

    private final BaggageClaimRepository baggageClaimRepository;
    private final UserRepository userRepository;
    private final FlightService flightService;

    @Transactional
    public BaggageClaimResponse createClaim(BaggageClaimRequest request, String passengerEmail) {
        User passenger = userRepository.findByEmail(passengerEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", passengerEmail));
        Flight flight = flightService.findById(request.getFlightId());

        BaggageClaim claim = BaggageClaim.builder()
                .passenger(passenger)
                .flight(flight)
                .description(request.getDescription())
                .build();

        return toResponse(baggageClaimRepository.save(claim));
    }

    public List<BaggageClaimResponse> getMyClaims(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));
        return baggageClaimRepository.findByPassenger(user).stream()
                .map(this::toResponse)
                .toList();
    }

    public List<BaggageClaimResponse> getAllClaims() {
        return baggageClaimRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public BaggageClaimResponse updateStatus(Long id, ClaimStatusUpdateRequest request) {
        BaggageClaim claim = baggageClaimRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("BaggageClaim", "id", id));
        claim.setStatus(request.getStatus());
        return toResponse(baggageClaimRepository.save(claim));
    }

    private BaggageClaimResponse toResponse(BaggageClaim claim) {
        return BaggageClaimResponse.builder()
                .id(claim.getId())
                .passengerId(claim.getPassenger().getId())
                .passengerName(claim.getPassenger().getName())
                .flightId(claim.getFlight().getId())
                .flightNumber(claim.getFlight().getFlightNumber())
                .description(claim.getDescription())
                .status(claim.getStatus())
                .createdAt(claim.getCreatedAt())
                .updatedAt(claim.getUpdatedAt())
                .build();
    }
}
