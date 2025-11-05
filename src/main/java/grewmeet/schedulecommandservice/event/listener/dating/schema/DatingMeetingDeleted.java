package grewmeet.schedulecommandservice.event.listener.dating.schema;

import java.time.LocalDateTime;
import java.util.UUID;

public record DatingMeetingDeleted(
        UUID meetingUuid,
        LocalDateTime deletedAt
) {}
