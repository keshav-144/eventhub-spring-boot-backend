package com.flowtech.eventhub_spring_boot_backend.dto;

public record EventStatusResponseDTO(
        Integer eventId,
        String status
) {}