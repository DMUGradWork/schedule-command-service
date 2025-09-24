package grewmeet.schedulecommandservice.event.schema;

import grewmeet.schedulecommandservice.domain.ScheduleSource;

import java.time.LocalDateTime;
import java.util.UUID;

public record ScheduleDeleted(
        UUID scheduleId,
        UUID ownerId,
        ScheduleSource source,
        long version,
        LocalDateTime occurredAt
) {}
