package grewmeet.schedulecommandservice.service;

import java.time.LocalDateTime;
import java.util.UUID;

public interface StudyScheduleService {

    void register(UUID userId,
                  UUID studyGroupId,
                  UUID meetingId,
                  String meetingName,
                  String description,
                  LocalDateTime startAt,
                  LocalDateTime endAt);

    void update(UUID userId,
                UUID meetingId,
                String newMeetingName,
                String newDescription,
                LocalDateTime newStartAt,
                LocalDateTime newEndAt);

    void delete(UUID userId,
                UUID meetingId);

    void reschedule(UUID studyGroupId,
                    UUID meetingId,
                    String newMeetingName,
                    String newDescription,
                    LocalDateTime newStartAt,
                    LocalDateTime newEndAt);

    void cancelMeeting(UUID studyGroupId,
                       UUID meetingId);
}
