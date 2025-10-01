package grewmeet.schedulecommandservice.event.listener.study;

import com.fasterxml.jackson.databind.ObjectMapper;
import grewmeet.schedulecommandservice.event.listener.study.schema.StudyMeetingEventParticipationRegistered;
import grewmeet.schedulecommandservice.event.listener.study.schema.StudyMeetingParticipationCancelled;
import grewmeet.schedulecommandservice.event.listener.study.schema.StudyMeetingParticipationCompleted;
import grewmeet.schedulecommandservice.event.listener.study.schema.StudyMeetingRescheduled;
import grewmeet.schedulecommandservice.event.listener.study.schema.StudyMeetingCancelled;
import grewmeet.schedulecommandservice.service.StudyScheduleService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StudyMeetingEventListener {

    private static final Logger log = LoggerFactory.getLogger(StudyMeetingEventListener.class);

    private final StudyScheduleService studyScheduleService;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "study.meeting.participation.registered", groupId = "schedule-command-service")
    public void onParticipationRegistered(String payload) {
        try {
            StudyMeetingEventParticipationRegistered event =
                    objectMapper.readValue(payload, StudyMeetingEventParticipationRegistered.class);
            studyScheduleService.register(
                    event.userId(),
                    event.studyGroupId(),
                    event.meetingId(),
                    event.meetingName(),
                    event.description(),
                    event.startAt(),
                    event.endAt()
            );
        } catch (Exception e) {
            log.error("Failed to parse Study Registered payload", e);
        }
    }

    @KafkaListener(topics = "study.meeting.participation.cancelled", groupId = "schedule-command-service")
    public void onParticipationCancelled(String payload) {
        try {
            StudyMeetingParticipationCancelled event =
                    objectMapper.readValue(payload, StudyMeetingParticipationCancelled.class);
            studyScheduleService.delete(event.userId(), event.meetingId());
        } catch (Exception e) {
            log.error("Failed to parse Study Cancelled payload", e);
        }
    }

    @KafkaListener(topics = "study.meeting.participation.completed", groupId = "schedule-command-service")
    public void onParticipationCompleted(String payload) {
        try {
            StudyMeetingParticipationCompleted event =
                    objectMapper.readValue(payload, StudyMeetingParticipationCompleted.class);
            studyScheduleService.delete(event.userId(), event.meetingId());
        } catch (Exception e) {
            log.error("Failed to parse Study Completed payload", e);
        }
    }

    @KafkaListener(topics = "study.meeting.rescheduled", groupId = "schedule-command-service")
    public void onMeetingRescheduled(String payload) {
        try {
            StudyMeetingRescheduled event = objectMapper.readValue(payload, StudyMeetingRescheduled.class);
            studyScheduleService.reschedule(
                    event.studyGroupId(),
                    event.meetingId(),
                    event.newMeetingName(),
                    event.newDescription(),
                    event.newStartAt(),
                    event.newEndAt()
            );
        } catch (Exception e) {
            log.error("Failed to parse Study Rescheduled payload", e);
        }
    }

    @KafkaListener(topics = "study.meeting.cancelled", groupId = "schedule-command-service")
    public void onMeetingCancelled(String payload) {
        try {
            StudyMeetingCancelled event = objectMapper.readValue(payload, StudyMeetingCancelled.class);
            studyScheduleService.cancelMeeting(event.studyGroupId(), event.meetingId());
        } catch (Exception e) {
            log.error("Failed to parse Study Meeting Cancelled payload", e);
        }
    }
}
