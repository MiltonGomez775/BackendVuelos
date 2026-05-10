package com.aeropuerto.flytrack.repository;

import com.aeropuerto.flytrack.entity.BaggageClaim;
import com.aeropuerto.flytrack.entity.User;
import com.aeropuerto.flytrack.enums.ClaimStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BaggageClaimRepository extends JpaRepository<BaggageClaim, Long> {
    List<BaggageClaim> findByPassenger(User passenger);
    List<BaggageClaim> findByStatus(ClaimStatus status);
}
