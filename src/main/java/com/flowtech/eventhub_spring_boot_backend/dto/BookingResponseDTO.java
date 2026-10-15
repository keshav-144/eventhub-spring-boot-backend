
package com.flowtech.eventhub_spring_boot_backend.dto;

import java.time.LocalDateTime;

public record BookingResponseDTO(
        int bookingId,
        int eventId,
        int quantity,
        double totalPrice,
        String paymentStatus,
        LocalDateTime paymentDateTime,
        LocalDateTime eventDateTime,
        LocalDateTime bookingDateTime
) {}
