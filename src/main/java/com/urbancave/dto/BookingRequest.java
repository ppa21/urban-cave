package com.urbancave.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record BookingRequest(
        @NotNull Long serviceId,
        @NotNull Long stylistId,
        @NotNull LocalDateTime startTime,
        @NotBlank String clientName,
        @Email String clientEmail
) {}
