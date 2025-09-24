package grewmeet.schedulecommandservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record CreateCustomScheduleRequest(
        @NotBlank String title,
        String description,
        @NotNull LocalDateTime startAt,
        @NotNull LocalDateTime endAt,
        String location,
        boolean allDay
) {}
