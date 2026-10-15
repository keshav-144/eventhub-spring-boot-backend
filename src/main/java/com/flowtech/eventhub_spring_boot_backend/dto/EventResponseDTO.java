package com.flowtech.eventhub_spring_boot_backend.dto;

import java.time.LocalDateTime;

import com.flowtech.eventhub_spring_boot_backend.enums.EventVerification;

public record EventResponseDTO(
        Integer eventId,
        int availableTickets,
        double ticketPrice,
        LocalDateTime eventDateTime,
        EventVerification status
) {}