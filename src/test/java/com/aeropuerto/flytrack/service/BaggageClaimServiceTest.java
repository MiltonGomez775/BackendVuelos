package com.aeropuerto.flytrack.service;

import com.aeropuerto.flytrack.dto.request.BaggageClaimRequest;
import com.aeropuerto.flytrack.dto.request.ClaimStatusUpdateRequest;
import com.aeropuerto.flytrack.dto.response.BaggageClaimResponse;
import com.aeropuerto.flytrack.entity.BaggageClaim;
import com.aeropuerto.flytrack.entity.Flight;
import com.aeropuerto.flytrack.entity.User;
import com.aeropuerto.flytrack.enums.ClaimStatus;
import com.aeropuerto.flytrack.enums.FlightStatus;
import com.aeropuerto.flytrack.enums.Role;
import com.aeropuerto.flytrack.exception.ResourceNotFoundException;
import com.aeropuerto.flytrack.repository.BaggageClaimRepository;
import com.aeropuerto.flytrack.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BaggageClaimServiceTest {

    @Mock private BaggageClaimRepository baggageClaimRepository;
    @Mock private UserRepository userRepository;
    @Mock private FlightService flightService;
    @InjectMocks private BaggageClaimService baggageClaimService;

    private User passenger;
    private Flight flight;
    private BaggageClaim claim;

    @BeforeEach
    void setUp() {
        passenger = User.builder()
                .id(1L).name("Jane Doe").email("jane@test.com").role(Role.PASSENGER).build();

        flight = Flight.builder()
                .id(1L).flightNumber("AV456").airline("Avianca")
                .origin("BOG").destination("CTG")
                .departureTime(LocalDateTime.now().plusHours(1))
                .arrivalTime(LocalDateTime.now().plusHours(2))
                .status(FlightStatus.SCHEDULED).gate("C1").availableSeats(80)
                .createdAt(LocalDateTime.now()).build();

        claim = BaggageClaim.builder()
                .id(1L).passenger(passenger).flight(flight)
                .description("Maleta dañada").status(ClaimStatus.OPEN)
                .createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now()).build();
    }

    @Test
    void createClaim_success() {
        BaggageClaimRequest request = new BaggageClaimRequest();
        request.setFlightId(1L);
        request.setDescription("Maleta dañada");

        when(userRepository.findByEmail("jane@test.com")).thenReturn(Optional.of(passenger));
        when(flightService.findById(1L)).thenReturn(flight);
        when(baggageClaimRepository.save(any(BaggageClaim.class))).thenReturn(claim);

        BaggageClaimResponse response = baggageClaimService.createClaim(request, "jane@test.com");

        assertThat(response.getDescription()).isEqualTo("Maleta dañada");
        assertThat(response.getStatus()).isEqualTo(ClaimStatus.OPEN);
        verify(baggageClaimRepository).save(any(BaggageClaim.class));
    }

    @Test
    void createClaim_userNotFound_throwsException() {
        BaggageClaimRequest request = new BaggageClaimRequest();
        request.setFlightId(1L);
        request.setDescription("Maleta perdida");

        when(userRepository.findByEmail("noexiste@test.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> baggageClaimService.createClaim(request, "noexiste@test.com"))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void getMyClaims_returnsList() {
        when(userRepository.findByEmail("jane@test.com")).thenReturn(Optional.of(passenger));
        when(baggageClaimRepository.findByPassenger(passenger)).thenReturn(List.of(claim));

        List<BaggageClaimResponse> responses = baggageClaimService.getMyClaims("jane@test.com");

        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).getFlightNumber()).isEqualTo("AV456");
    }

    @Test
    void updateStatus_success() {
        ClaimStatusUpdateRequest req = new ClaimStatusUpdateRequest();
        req.setStatus(ClaimStatus.IN_REVIEW);

        when(baggageClaimRepository.findById(1L)).thenReturn(Optional.of(claim));
        when(baggageClaimRepository.save(any(BaggageClaim.class))).thenReturn(claim);

        BaggageClaimResponse response = baggageClaimService.updateStatus(1L, req);

        assertThat(response).isNotNull();
        verify(baggageClaimRepository).save(any(BaggageClaim.class));
    }

    @Test
    void updateStatus_notFound_throwsException() {
        ClaimStatusUpdateRequest req = new ClaimStatusUpdateRequest();
        req.setStatus(ClaimStatus.RESOLVED);

        when(baggageClaimRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> baggageClaimService.updateStatus(99L, req))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
