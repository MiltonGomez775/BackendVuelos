package com.aeropuerto.flytrack.repository;

import com.aeropuerto.flytrack.entity.Flight;
import com.aeropuerto.flytrack.enums.FlightStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FlightRepository extends JpaRepository<Flight, Long> {
    Optional<Flight> findByFlightNumber(String flightNumber);
    List<Flight> findByStatus(FlightStatus status);
    boolean existsByFlightNumber(String flightNumber);
}
