package com.aeropuerto.flytrack.service;

import com.aeropuerto.flytrack.dto.request.FlightRequest;
import com.aeropuerto.flytrack.dto.request.FlightStatusUpdateRequest;
import com.aeropuerto.flytrack.dto.response.FlightResponse;
import com.aeropuerto.flytrack.entity.Flight;
import com.aeropuerto.flytrack.enums.FlightStatus;
import com.aeropuerto.flytrack.exception.ResourceNotFoundException;
import com.aeropuerto.flytrack.repository.FlightRepository;
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
class FlightServiceTest {

    @Mock private FlightRepository flightRepository;
    @Mock private NotificationService notificationService;
    @InjectMocks private FlightService flightService;

    private Flight flight;
    private FlightRequest flightRequest;

    @BeforeEach
    void setUp() {
        flight = Flight.builder()
                .id(1L).flightNumber("AV123").airline("Avianca")
                .origin("BOG").destination("MDE")
                .departureTime(LocalDateTime.now().plusHours(2))
                .arrivalTime(LocalDateTime.now().plusHours(3))
                .status(FlightStatus.SCHEDULED).gate("A5")
                .availableSeats(120).createdAt(LocalDateTime.now()).build();

        flightRequest = new FlightRequest();
        flightRequest.setFlightNumber("AV123");
        flightRequest.setAirline("Avianca");
        flightRequest.setOrigin("BOG");
        flightRequest.setDestination("MDE");
        flightRequest.setDepartureTime(LocalDateTime.now().plusHours(2));
        flightRequest.setArrivalTime(LocalDateTime.now().plusHours(3));
        flightRequest.setGate("A5");
        flightRequest.setAvailableSeats(120);
    }

    @Test
    void createFlight_success() {
        when(flightRepository.existsByFlightNumber("AV123")).thenReturn(false);
        when(flightRepository.save(any(Flight.class))).thenReturn(flight);

        FlightResponse response = flightService.createFlight(flightRequest);

        assertThat(response.getFlightNumber()).isEqualTo("AV123");
        assertThat(response.getAirline()).isEqualTo("Avianca");
        verify(flightRepository).save(any(Flight.class));
    }

    @Test
    void createFlight_duplicateNumber_throwsException() {
        when(flightRepository.existsByFlightNumber("AV123")).thenReturn(true);

        assertThatThrownBy(() -> flightService.createFlight(flightRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("AV123");

        verify(flightRepository, never()).save(any());
    }

    @Test
    void getAllFlights_returnsList() {
        when(flightRepository.findAll()).thenReturn(List.of(flight));

        List<FlightResponse> responses = flightService.getAllFlights();

        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).getFlightNumber()).isEqualTo("AV123");
    }

    @Test
    void getFlightById_found() {
        when(flightRepository.findById(1L)).thenReturn(Optional.of(flight));

        FlightResponse response = flightService.getFlightById(1L);

        assertThat(response.getId()).isEqualTo(1L);
    }

    @Test
    void getFlightById_notFound_throwsException() {
        when(flightRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> flightService.getFlightById(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void updateFlightStatus_triggersNotification() {
        FlightStatusUpdateRequest req = new FlightStatusUpdateRequest();
        req.setStatus(FlightStatus.BOARDING);
        req.setGate("B3");

        when(flightRepository.findById(1L)).thenReturn(Optional.of(flight));
        when(flightRepository.save(any(Flight.class))).thenReturn(flight);

        flightService.updateFlightStatus(1L, req);

        verify(notificationService).notifyFlightUpdate(any(Flight.class));
    }

    @Test
    void deleteFlight_success() {
        when(flightRepository.findById(1L)).thenReturn(Optional.of(flight));
        doNothing().when(flightRepository).deleteById(1L);

        flightService.deleteFlight(1L);

        verify(flightRepository).deleteById(1L);
    }

    @Test
    void deleteFlight_notFound_throwsException() {
        when(flightRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> flightService.deleteFlight(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
