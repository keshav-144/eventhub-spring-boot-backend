package com.flowtech.eventhub_spring_boot_backend.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.flowtech.eventhub_spring_boot_backend.dto.BookingResponseDTO;
import com.flowtech.eventhub_spring_boot_backend.dto.EventResponseDTO;
import com.flowtech.eventhub_spring_boot_backend.entity.Booking;
import com.flowtech.eventhub_spring_boot_backend.entity.Event;
import com.flowtech.eventhub_spring_boot_backend.entity.User;
import com.flowtech.eventhub_spring_boot_backend.enums.EventVerification;
import com.flowtech.eventhub_spring_boot_backend.exception.TicketNotAvailableException;
import com.flowtech.eventhub_spring_boot_backend.repository.BookingRepository;
import com.flowtech.eventhub_spring_boot_backend.repository.EventRepository;
import com.flowtech.eventhub_spring_boot_backend.repository.UserRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EventService {

	private final EventRepository eventRepository;

	private final UserRepository repository;

	private final BookingRepository bookingRepository;

	public ResponseEntity<?> registerEvent(
	        Event event,
	        Authentication authentication) {

	    if (authentication == null || !authentication.isAuthenticated()) {
	        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
	                .body("Please login first");
	    }

	    boolean isOrganiser = authentication.getAuthorities()
	            .stream()
	            .anyMatch(authority ->
	                    authority.getAuthority().equals("ROLE_ORGANISER"));

	    if (!isOrganiser) {
	        return ResponseEntity.status(HttpStatus.FORBIDDEN)
	                .body("Only organisers can register events");
	    }

	    String email = authentication.getName();

	    User organiser = repository.findByEmail(email)
	            .orElseThrow(() ->
	                    new RuntimeException("Organiser not found"));

	    event.setUser(organiser);
	    event.setStatus(EventVerification.PENDING);

	    Event savedEvent = eventRepository.save(event);

	    EventResponseDTO response = toEventResponseDTO(savedEvent);

	    return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}
	@Transactional
	public Booking bookEvent(Integer eventId, int ticketQuantity, Authentication authentication) {

		String customerEmail = authentication.getName();

		User user = repository.findByEmail(customerEmail).orElseThrow(() -> new RuntimeException("User not found"));

		Event event = eventRepository.findById(eventId).orElse(null);

		if (event != null && event.getStatus() == EventVerification.APPROVED) {

			int availabeTicket = event.getAvailableTickets();

			if (ticketQuantity <= availabeTicket && ticketQuantity > 0) {

				Booking booking = new Booking();
				booking.setEvent(event);
				booking.setUser(user);
				booking.setQuantity(ticketQuantity);
				booking.setTotalPrice(ticketQuantity * event.getTicketPrice());
				booking.setPaymentStatus("PAID");
				booking.setPaymentDateTime(LocalDateTime.now());
				booking.setEventDateTime(event.getEventDateTime());
				booking.setBookingDateTime(LocalDateTime.now());

				bookingRepository.save(booking);

				event.setAvailableTickets(availabeTicket - ticketQuantity);

				eventRepository.save(event);

				return booking;
			} else {
				System.out.println("Not enough tickets available");
				throw new TicketNotAvailableException("ticket not available it has been sold out...."); // or throw an
																										// exception if
																										// not enough																							// tickets are
																										// available
			}
		}
		return null; // or throw an exception if the event is not found or not approved
	}

	public List<Event> findEventByStatusPending() {
		return eventRepository.findByStatus(EventVerification.PENDING);
	}

	public Event find(Integer eventId) {
		return eventRepository.findById(eventId).orElse(null);
	}

	public List<Event> getAllApprovedEvents() {
		return eventRepository.findByStatus(EventVerification.APPROVED);
	}

	public List<Event> getAllRejectedEvents() {
		return eventRepository.findByStatus(EventVerification.REJECTED);
	}

	public EventResponseDTO toEventResponseDTO(Event event) {
	    return new EventResponseDTO(
	            event.getId(),
	            event.getAvailableTickets(),
	            event.getTicketPrice(),
	            event.getEventDateTime(),
	            event.getStatus()
	    );
	}

	public BookingResponseDTO toBookingResponseDTO(Booking booking) {
		return new BookingResponseDTO(booking.getBookingId(), booking.getEvent().getId(), booking.getQuantity(),
				booking.getTotalPrice(), booking.getPaymentStatus(), booking.getPaymentDateTime(),
				booking.getEventDateTime(), booking.getBookingDateTime());
	}

}
