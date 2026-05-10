package com.aeropuerto.flytrack.service;

import com.aeropuerto.flytrack.dto.request.ItineraryRequest;
import com.aeropuerto.flytrack.dto.response.ItineraryResponse;
import com.aeropuerto.flytrack.entity.Flight;
import com.aeropuerto.flytrack.entity.Itinerary;
import com.aeropuerto.flytrack.entity.User;
import com.aeropuerto.flytrack.exception.ResourceNotFoundException;
import com.aeropuerto.flytrack.repository.ItineraryRepository;
import com.aeropuerto.flytrack.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ItineraryService {

    private final ItineraryRepository itineraryRepository;
    private final UserRepository userRepository;
    private final FlightService flightService;

    @Transactional
    public ItineraryResponse createItinerary(ItineraryRequest request, String passengerEmail) {
        User passenger = userRepository.findByEmail(passengerEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", passengerEmail));
        Flight flight = flightService.findById(request.getFlightId());

        if (itineraryRepository.existsByPassengerAndFlight(passenger, flight)) {
            throw new IllegalArgumentException("Passenger already has an itinerary for this flight");
        }

        Itinerary itinerary = Itinerary.builder()
                .passenger(passenger)
                .flight(flight)
                .seatNumber(request.getSeatNumber())
                .bookingCode(UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                .build();

        return toResponse(itineraryRepository.save(itinerary));
    }

    public List<ItineraryResponse> getMyItineraries(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));
        return itineraryRepository.findByPassenger(user).stream()
                .map(this::toResponse)
                .toList();
    }

    public List<ItineraryResponse> getItinerariesByFlight(Long flightId) {
        Flight flight = flightService.findById(flightId);
        return itineraryRepository.findByFlight(flight).stream()
                .map(this::toResponse)
                .toList();
    }

    private ItineraryResponse toResponse(Itinerary itinerary) {
        return ItineraryResponse.builder()
                .id(itinerary.getId())
                .passengerId(itinerary.getPassenger().getId())
                .passengerName(itinerary.getPassenger().getName())
                .flight(flightService.toResponse(itinerary.getFlight()))
                .seatNumber(itinerary.getSeatNumber())
                .bookingCode(itinerary.getBookingCode())
                .createdAt(itinerary.getCreatedAt())
                .build();
    }
}
