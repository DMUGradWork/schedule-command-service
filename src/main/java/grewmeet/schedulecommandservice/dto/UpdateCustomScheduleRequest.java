package grewmeet.schedulecommandservice.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record UpdateCustomScheduleRequest(
        UUID scheduleId,
        String title,
        String description,
        LocalDateTime startAt,
        LocalDateTime endAt,
        String location,
        Boolean allDay
) {}
