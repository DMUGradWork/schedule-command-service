package grewmeet.schedulecommandservice.event.listener.study.schema;

import java.time.LocalDateTime;
import java.util.UUID;

public record StudyMeetingEventParticipationRegistered(
        UUID studyGroupId,
        UUID meetingId,
        UUID userId,
        String studyGroupName,
        String meetingName,
        String description,
        LocalDateTime startAt,
        LocalDateTime endAt,
        LocalDateTime joinedAt
) {}

