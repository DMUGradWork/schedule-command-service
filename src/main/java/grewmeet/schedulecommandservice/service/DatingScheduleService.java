package grewmeet.schedulecommandservice.service;

import java.time.LocalDateTime;
import java.util.UUID;

public interface DatingScheduleService {

    void joinParticipant(UUID authUserId,
                         UUID meetingUuid,
                         String meetingTitle,
                         LocalDateTime meetingDateTime);

    void leaveParticipant(UUID authUserId,
                          UUID meetingUuid);

    void updateMeeting(UUID meetingUuid,
                       String title,
                       String description,
                       LocalDateTime meetingDateTime);

    void deleteMeeting(UUID meetingUuid);
}
