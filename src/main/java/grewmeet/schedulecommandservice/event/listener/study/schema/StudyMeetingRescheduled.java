package grewmeet.schedulecommandservice.event.listener.study.schema;

import java.time.LocalDateTime;
import java.util.UUID;

public record StudyMeetingRescheduled(
        UUID studyGroupId,
        UUID meetingId,
        String newMeetingName,
        String newDescription,
        LocalDateTime newStartAt,
        LocalDateTime newEndAt,
        LocalDateTime updatedAt
) {}

