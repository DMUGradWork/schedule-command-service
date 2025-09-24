package grewmeet.schedulecommandservice.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.UUID;

public record UpdateCustomScheduleRequest(
        @NotNull UUID scheduleId,
        String title,
        String description,
        LocalDateTime startAt,
        LocalDateTime endAt,
        String location,
        Boolean allDay
) {}
