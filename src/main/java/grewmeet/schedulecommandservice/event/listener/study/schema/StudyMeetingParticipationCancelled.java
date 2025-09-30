package grewmeet.schedulecommandservice.event.listener.study.schema;

import java.util.UUID;

public record StudyMeetingParticipationCancelled(
        UUID studyGroupId,
        UUID meetingId,
        UUID userId
) {}

