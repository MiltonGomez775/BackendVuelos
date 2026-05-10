package com.aeropuerto.flytrack.config;

import com.aeropuerto.flytrack.entity.User;
import com.aeropuerto.flytrack.enums.Role;
import com.aeropuerto.flytrack.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        crearUsuarioSiNoExiste("Admin FlyTrack", "admin@flytrack.com", "admin123", Role.ADMIN);
        crearUsuarioSiNoExiste("Operador FlyTrack", "operador@flytrack.com", "operador123", Role.OPERATOR);
        crearUsuarioSiNoExiste("Pasajero Demo", "pasajero@flytrack.com", "pasajero123", Role.PASSENGER);
    }

    private void crearUsuarioSiNoExiste(String name, String email, String password, Role role) {
        if (!userRepository.existsByEmail(email)) {
            userRepository.save(User.builder()
                    .name(name)
                    .email(email)
                    .password(passwordEncoder.encode(password))
                    .role(role)
                    .build());
            System.out.println("Usuario creado: " + email + " / " + password + " [" + role + "]");
        }
    }
}
