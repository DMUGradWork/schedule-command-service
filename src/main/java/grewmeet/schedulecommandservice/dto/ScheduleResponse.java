package grewmeet.schedulecommandservice.dto;

import grewmeet.schedulecommandservice.domain.Schedule;
import grewmeet.schedulecommandservice.domain.ScheduleSource;

import java.time.LocalDateTime;
import java.util.UUID;

public record ScheduleResponse(
        Long id,
        UUID scheduleId,
        UUID ownerId,
        String title,
        String description,
        LocalDateTime startAt,
        LocalDateTime endAt
) {
    public static ScheduleResponse from(Schedule s) {
        return new ScheduleResponse(
                s.getId(),
                s.getScheduleId(),
                s.getOwnerId(),
                s.getTitle(),
                s.getDescription(),
                s.getStartAt(),
                s.getEndAt()
        );
    }
}
