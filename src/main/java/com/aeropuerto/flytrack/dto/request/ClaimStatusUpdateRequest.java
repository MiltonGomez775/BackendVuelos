package com.aeropuerto.flytrack.dto.request;

import com.aeropuerto.flytrack.enums.ClaimStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ClaimStatusUpdateRequest {

    @NotNull(message = "Status is required")
    private ClaimStatus status;
}
