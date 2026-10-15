package com.flowtech.eventhub_spring_boot_backend.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.flowtech.eventhub_spring_boot_backend.dto.BookingResponseDTO;
import com.flowtech.eventhub_spring_boot_backend.dto.EventResponseDTO;
import com.flowtech.eventhub_spring_boot_backend.entity.Booking;
import com.flowtech.eventhub_spring_boot_backend.entity.Event;
import com.flowtech.eventhub_spring_boot_backend.service.EventService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(value = "/customer")
@RequiredArgsConstructor
public class CustomerController {

	private final EventService eventService;
	
	@GetMapping("/getAllApprovedEvents")
	public ResponseEntity<List<EventResponseDTO>> getAllApprovedEvents() {

	    List<EventResponseDTO> events =
	            eventService.getAllApprovedEvents()
	                    .stream()
	                    .map(eventService::toEventResponseDTO)
	                    .toList();

	    return ResponseEntity.ok(events);
	}
	
	@PostMapping("/bookEvent")
	public ResponseEntity<BookingResponseDTO> bookEvent(
	        @RequestParam int eventId,
	        @RequestParam int ticketQuantity,
	        Authentication authentication) {

	    Booking booking = eventService.bookEvent(
	            eventId, ticketQuantity, authentication);

	    BookingResponseDTO response =
	            eventService.toBookingResponseDTO(booking);

	    return ResponseEntity.ok(response);
	}
	
}
