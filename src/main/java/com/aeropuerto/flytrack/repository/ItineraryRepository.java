package com.aeropuerto.flytrack.repository;

import com.aeropuerto.flytrack.entity.Flight;
import com.aeropuerto.flytrack.entity.Itinerary;
import com.aeropuerto.flytrack.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ItineraryRepository extends JpaRepository<Itinerary, Long> {
    List<Itinerary> findByPassenger(User passenger);
    List<Itinerary> findByFlight(Flight flight);
    boolean existsByPassengerAndFlight(User passenger, Flight flight);
}
