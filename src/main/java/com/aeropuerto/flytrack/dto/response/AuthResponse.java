package com.aeropuerto.flytrack.dto.response;

import com.aeropuerto.flytrack.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
    private String token;
    private String type;
    private String email;
    private String name;
    private Role role;
}
