package grewmeet.schedulecommandservice.event.listener.study.schema;

import java.time.LocalDateTime;
import java.util.UUID;

public record StudyMeetingParticipationCompleted(
        UUID studyGroupId,
        UUID meetingId,
        UUID userId,
        String studyGroupName,
        String meetingName,
        LocalDateTime completedAt
) {}

