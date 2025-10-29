package grewmeet.schedulecommandservice.event.schema;

import grewmeet.schedulecommandservice.domain.ScheduleSource;

import java.time.LocalDateTime;
import java.util.UUID;

public record ScheduleUpdated(
        UUID scheduleId,
        UUID ownerId,
        String title,
        String description,
        LocalDateTime startAt,
        LocalDateTime endAt,
        ScheduleSource source,
        UUID studyGroupId,
        UUID meetingId,
        long version,
        LocalDateTime occurredAt
) {}
