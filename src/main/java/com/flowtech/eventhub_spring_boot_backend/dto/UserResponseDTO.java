package com.flowtech.eventhub_spring_boot_backend.dto;

import java.time.LocalDate;

public record UserResponseDTO(
        Integer id,
        String name,
        String email,
        Long phone,
        LocalDate createdAt,
        String roleName
) {}
