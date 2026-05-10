package com.aeropuerto.flytrack.service;

import com.aeropuerto.flytrack.dto.request.FlightRequest;
import com.aeropuerto.flytrack.dto.request.FlightStatusUpdateRequest;
import com.aeropuerto.flytrack.dto.response.FlightResponse;
import com.aeropuerto.flytrack.entity.Flight;
import com.aeropuerto.flytrack.exception.ResourceNotFoundException;
import com.aeropuerto.flytrack.repository.FlightRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FlightService {

    private final FlightRepository flightRepository;
    private final NotificationService notificationService;

    public FlightResponse createFlight(FlightRequest request) {
        if (flightRepository.existsByFlightNumber(request.getFlightNumber())) {
            throw new IllegalArgumentException("Flight number already exists: " + request.getFlightNumber());
        }

        Flight flight = Flight.builder()
                .flightNumber(request.getFlightNumber())
                .airline(request.getAirline())
                .origin(request.getOrigin())
                .destination(request.getDestination())
                .departureTime(request.getDepartureTime())
                .arrivalTime(request.getArrivalTime())
                .gate(request.getGate())
                .availableSeats(request.getAvailableSeats())
                .build();

        return toResponse(flightRepository.save(flight));
    }

    public List<FlightResponse> getAllFlights() {
        return flightRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    public FlightResponse getFlightById(Long id) {
        return toResponse(findById(id));
    }

    @Transactional
    public FlightResponse updateFlight(Long id, FlightRequest request) {
        Flight flight = findById(id);
        flight.setFlightNumber(request.getFlightNumber());
        flight.setAirline(request.getAirline());
        flight.setOrigin(request.getOrigin());
        flight.setDestination(request.getDestination());
        flight.setDepartureTime(request.getDepartureTime());
        flight.setArrivalTime(request.getArrivalTime());
        flight.setGate(request.getGate());
        flight.setAvailableSeats(request.getAvailableSeats());
        return toResponse(flightRepository.save(flight));
    }

    @Transactional
    public FlightResponse updateFlightStatus(Long id, FlightStatusUpdateRequest request) {
        Flight flight = findById(id);
        flight.setStatus(request.getStatus());
        if (request.getGate() != null) {
            flight.setGate(request.getGate());
        }
        Flight saved = flightRepository.save(flight);
        notificationService.notifyFlightUpdate(saved);
        return toResponse(saved);
    }

    public void deleteFlight(Long id) {
        findById(id);
        flightRepository.deleteById(id);
    }

    public Flight findById(Long id) {
        return flightRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Flight", "id", id));
    }

    public FlightResponse toResponse(Flight flight) {
        return FlightResponse.builder()
                .id(flight.getId())
                .flightNumber(flight.getFlightNumber())
                .airline(flight.getAirline())
                .origin(flight.getOrigin())
                .destination(flight.getDestination())
                .departureTime(flight.getDepartureTime())
                .arrivalTime(flight.getArrivalTime())
                .status(flight.getStatus())
                .gate(flight.getGate())
                .availableSeats(flight.getAvailableSeats())
                .createdAt(flight.getCreatedAt())
                .build();
    }
}
