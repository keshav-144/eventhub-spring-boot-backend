package com.flowtech.eventhub_spring_boot_backend.dto;

import java.time.LocalDateTime;

public record ErrorResponseDTO(
        String message,
        LocalDateTime timestamp
) {}