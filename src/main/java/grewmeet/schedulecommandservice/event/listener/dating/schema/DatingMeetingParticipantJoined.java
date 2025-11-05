package grewmeet.schedulecommandservice.event.listener.dating.schema;

import java.time.LocalDateTime;
import java.util.UUID;

public record DatingMeetingParticipantJoined(
        UUID meetingUuid,
        UUID authUserId,
        String gender,
        String meetingTitle,
        LocalDateTime meetingDateTime,
        LocalDateTime joinedAt
) {}
