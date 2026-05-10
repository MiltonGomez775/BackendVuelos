package com.aeropuerto.flytrack.service;

import com.aeropuerto.flytrack.dto.response.NotificationResponse;
import com.aeropuerto.flytrack.entity.Flight;
import com.aeropuerto.flytrack.entity.Itinerary;
import com.aeropuerto.flytrack.entity.Notification;
import com.aeropuerto.flytrack.entity.User;
import com.aeropuerto.flytrack.exception.ResourceNotFoundException;
import com.aeropuerto.flytrack.repository.ItineraryRepository;
import com.aeropuerto.flytrack.repository.NotificationRepository;
import com.aeropuerto.flytrack.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final ItineraryRepository itineraryRepository;
    private final UserRepository userRepository;
    private final SimpMessagingTemplate messagingTemplate;

    @Transactional
    public void notifyFlightUpdate(Flight flight) {
        String message = String.format("Flight %s status changed to %s. Gate: %s",
                flight.getFlightNumber(), flight.getStatus(), flight.getGate());

        List<Itinerary> itineraries = itineraryRepository.findByFlight(flight);
        for (Itinerary itinerary : itineraries) {
            Notification notification = Notification.builder()
                    .user(itinerary.getPassenger())
                    .flight(flight)
                    .message(message)
                    .build();
            notificationRepository.save(notification);
        }

        NotificationResponse wsPayload = NotificationResponse.builder()
                .message(message)
                .flightId(flight.getId())
                .flightNumber(flight.getFlightNumber())
                .build();

        messagingTemplate.convertAndSend("/topic/flights/" + flight.getId(), wsPayload);
    }

    public List<NotificationResponse> getUserNotifications(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));
        return notificationRepository.findByUserOrderByCreatedAtDesc(user)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public void markAsRead(Long notificationId, String email) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification", "id", notificationId));
        if (!notification.getUser().getEmail().equals(email)) {
            throw new SecurityException("Access denied to this notification");
        }
        notification.setRead(true);
        notificationRepository.save(notification);
    }

    public long countUnread(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));
        return notificationRepository.countByUserAndReadFalse(user);
    }

    private NotificationResponse toResponse(Notification n) {
        return NotificationResponse.builder()
                .id(n.getId())
                .message(n.getMessage())
                .flightId(n.getFlight() != null ? n.getFlight().getId() : null)
                .flightNumber(n.getFlight() != null ? n.getFlight().getFlightNumber() : null)
                .read(n.isRead())
                .createdAt(n.getCreatedAt())
                .build();
    }
}
