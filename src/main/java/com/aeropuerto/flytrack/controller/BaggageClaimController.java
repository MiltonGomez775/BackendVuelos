package com.aeropuerto.flytrack.controller;

import com.aeropuerto.flytrack.dto.request.BaggageClaimRequest;
import com.aeropuerto.flytrack.dto.request.ClaimStatusUpdateRequest;
import com.aeropuerto.flytrack.dto.response.BaggageClaimResponse;
import com.aeropuerto.flytrack.service.BaggageClaimService;
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
@RequestMapping("/api/baggage-claims")
@RequiredArgsConstructor
public class BaggageClaimController {

    private final BaggageClaimService baggageClaimService;

    @PostMapping
    @PreAuthorize("hasRole('PASSENGER')")
    public ResponseEntity<BaggageClaimResponse> createClaim(
            @Valid @RequestBody BaggageClaimRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(baggageClaimService.createClaim(request, userDetails.getUsername()));
    }

    @GetMapping("/my")
    @PreAuthorize("hasRole('PASSENGER')")
    public ResponseEntity<List<BaggageClaimResponse>> getMyClaims(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(baggageClaimService.getMyClaims(userDetails.getUsername()));
    }

    @GetMapping
    @PreAuthorize("hasRole('OPERATOR') or hasRole('ADMIN')")
    public ResponseEntity<List<BaggageClaimResponse>> getAllClaims() {
        return ResponseEntity.ok(baggageClaimService.getAllClaims());
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('OPERATOR') or hasRole('ADMIN')")
    public ResponseEntity<BaggageClaimResponse> updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody ClaimStatusUpdateRequest request) {
        return ResponseEntity.ok(baggageClaimService.updateStatus(id, request));
    }
}
