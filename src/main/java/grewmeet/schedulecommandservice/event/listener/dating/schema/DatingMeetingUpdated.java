package grewmeet.schedulecommandservice.event.listener.dating.schema;

import java.time.LocalDateTime;
import java.util.UUID;

public record DatingMeetingUpdated(
        UUID meetingUuid,
        String title,
        String description,
        LocalDateTime meetingDateTime,
        String location,
        Integer maxMaleParticipants,
        Integer maxFemaleParticipants,
        Integer currentMaleParticipants,
        Integer currentFemaleParticipants,
        LocalDateTime updatedAt
) {}
