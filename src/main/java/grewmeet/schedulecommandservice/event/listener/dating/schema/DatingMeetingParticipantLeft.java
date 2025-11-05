package grewmeet.schedulecommandservice.event.listener.dating.schema;

import java.time.LocalDateTime;
import java.util.UUID;

public record DatingMeetingParticipantLeft(
        UUID meetingUuid,
        UUID authUserId,
        String gender,
        LocalDateTime leftAt
) {}
