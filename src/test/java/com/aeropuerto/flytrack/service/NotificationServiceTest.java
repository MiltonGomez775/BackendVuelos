package com.aeropuerto.flytrack.service;

import com.aeropuerto.flytrack.dto.response.NotificationResponse;
import com.aeropuerto.flytrack.entity.Flight;
import com.aeropuerto.flytrack.entity.Itinerary;
import com.aeropuerto.flytrack.entity.Notification;
import com.aeropuerto.flytrack.entity.User;
import com.aeropuerto.flytrack.enums.FlightStatus;
import com.aeropuerto.flytrack.enums.Role;
import com.aeropuerto.flytrack.exception.ResourceNotFoundException;
import com.aeropuerto.flytrack.repository.ItineraryRepository;
import com.aeropuerto.flytrack.repository.NotificationRepository;
import com.aeropuerto.flytrack.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock private NotificationRepository notificationRepository;
    @Mock private ItineraryRepository itineraryRepository;
    @Mock private UserRepository userRepository;
    @Mock private SimpMessagingTemplate messagingTemplate;
    @InjectMocks private NotificationService notificationService;

    private User passenger;
    private Flight flight;
    private Notification notification;

    @BeforeEach
    void setUp() {
        passenger = User.builder()
                .id(1L).name("Carlos").email("carlos@test.com").role(Role.PASSENGER).build();

        flight = Flight.builder()
                .id(1L).flightNumber("LA789").airline("LATAM")
                .origin("MDE").destination("BOG")
                .departureTime(LocalDateTime.now().plusHours(3))
                .arrivalTime(LocalDateTime.now().plusHours(4))
                .status(FlightStatus.DELAYED).gate("D2")
                .createdAt(LocalDateTime.now()).build();

        notification = Notification.builder()
                .id(1L).user(passenger).flight(flight)
                .message("Flight LA789 status changed to DELAYED. Gate: D2")
                .read(false).createdAt(LocalDateTime.now()).build();
    }

    @Test
    void notifyFlightUpdate_savesNotificationsAndBroadcasts() {
        Itinerary itinerary = Itinerary.builder()
                .id(1L).passenger(passenger).flight(flight)
                .seatNumber("12A").bookingCode("ABC12345")
                .createdAt(LocalDateTime.now()).build();

        when(itineraryRepository.findByFlight(flight)).thenReturn(List.of(itinerary));
        when(notificationRepository.save(any(Notification.class))).thenReturn(notification);

        notificationService.notifyFlightUpdate(flight);

        verify(notificationRepository, times(1)).save(any(Notification.class));
        verify(messagingTemplate).convertAndSend(eq("/topic/flights/1"), any(NotificationResponse.class));
    }

    @Test
    void notifyFlightUpdate_noPassengers_onlyBroadcasts() {
        when(itineraryRepository.findByFlight(flight)).thenReturn(List.of());

        notificationService.notifyFlightUpdate(flight);

        verify(notificationRepository, never()).save(any());
        verify(messagingTemplate).convertAndSend(eq("/topic/flights/1"), any(NotificationResponse.class));
    }

    @Test
    void getUserNotifications_returnsOrderedList() {
        when(userRepository.findByEmail("carlos@test.com")).thenReturn(Optional.of(passenger));
        when(notificationRepository.findByUserOrderByCreatedAtDesc(passenger)).thenReturn(List.of(notification));

        List<NotificationResponse> responses = notificationService.getUserNotifications("carlos@test.com");

        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).getMessage()).contains("LA789");
    }

    @Test
    void markAsRead_success() {
        when(notificationRepository.findById(1L)).thenReturn(Optional.of(notification));
        when(notificationRepository.save(any(Notification.class))).thenReturn(notification);

        notificationService.markAsRead(1L, "carlos@test.com");

        verify(notificationRepository).save(any(Notification.class));
    }

    @Test
    void markAsRead_wrongUser_throwsSecurityException() {
        when(notificationRepository.findById(1L)).thenReturn(Optional.of(notification));

        assertThatThrownBy(() -> notificationService.markAsRead(1L, "otro@test.com"))
                .isInstanceOf(SecurityException.class);
    }

    @Test
    void countUnread_returnsCount() {
        when(userRepository.findByEmail("carlos@test.com")).thenReturn(Optional.of(passenger));
        when(notificationRepository.countByUserAndReadFalse(passenger)).thenReturn(3L);

        assertThat(notificationService.countUnread("carlos@test.com")).isEqualTo(3L);
    }

    @Test
    void getUserNotifications_userNotFound_throwsException() {
        when(userRepository.findByEmail("noexiste@test.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> notificationService.getUserNotifications("noexiste@test.com"))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
