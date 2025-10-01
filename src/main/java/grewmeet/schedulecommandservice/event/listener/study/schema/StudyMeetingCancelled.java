package grewmeet.schedulecommandservice.event.listener.study.schema;

import java.util.UUID;

public record StudyMeetingCancelled(
        UUID studyGroupId,
        UUID meetingId
) {}

