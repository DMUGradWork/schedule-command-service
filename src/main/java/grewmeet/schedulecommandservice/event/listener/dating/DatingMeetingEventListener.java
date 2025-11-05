package grewmeet.schedulecommandservice.event.listener.dating;

import com.fasterxml.jackson.databind.ObjectMapper;
import grewmeet.schedulecommandservice.event.listener.dating.schema.DatingMeetingDeleted;
import grewmeet.schedulecommandservice.event.listener.dating.schema.DatingMeetingParticipantJoined;
import grewmeet.schedulecommandservice.event.listener.dating.schema.DatingMeetingParticipantLeft;
import grewmeet.schedulecommandservice.event.listener.dating.schema.DatingMeetingUpdated;
import grewmeet.schedulecommandservice.service.DatingScheduleService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DatingMeetingEventListener {

    private static final Logger log = LoggerFactory.getLogger(DatingMeetingEventListener.class);

    private final DatingScheduleService datingScheduleService;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "dating.meeting.participant.joined", groupId = "schedule-command-service")
    public void onParticipantJoined(String payload) {
        try {
            DatingMeetingParticipantJoined event =
                    objectMapper.readValue(payload, DatingMeetingParticipantJoined.class);
            datingScheduleService.joinParticipant(
                    event.authUserId(),
                    event.meetingUuid(),
                    event.meetingTitle(),
                    event.meetingDateTime()
            );
        } catch (Exception e) {
            log.error("Failed to parse Dating Participant Joined payload", e);
        }
    }

    @KafkaListener(topics = "dating.meeting.participant.left", groupId = "schedule-command-service")
    public void onParticipantLeft(String payload) {
        try {
            DatingMeetingParticipantLeft event =
                    objectMapper.readValue(payload, DatingMeetingParticipantLeft.class);
            datingScheduleService.leaveParticipant(
                    event.authUserId(),
                    event.meetingUuid()
            );
        } catch (Exception e) {
            log.error("Failed to parse Dating Participant Left payload", e);
        }
    }

    @KafkaListener(topics = "dating.meeting.updated", groupId = "schedule-command-service")
    public void onMeetingUpdated(String payload) {
        try {
            DatingMeetingUpdated event =
                    objectMapper.readValue(payload, DatingMeetingUpdated.class);
            datingScheduleService.updateMeeting(
                    event.meetingUuid(),
                    event.title(),
                    event.description(),
                    event.meetingDateTime()
            );
        } catch (Exception e) {
            log.error("Failed to parse Dating Meeting Updated payload", e);
        }
    }

    @KafkaListener(topics = "dating.meeting.deleted", groupId = "schedule-command-service")
    public void onMeetingDeleted(String payload) {
        try {
            DatingMeetingDeleted event =
                    objectMapper.readValue(payload, DatingMeetingDeleted.class);
            datingScheduleService.deleteMeeting(event.meetingUuid());
        } catch (Exception e) {
            log.error("Failed to parse Dating Meeting Deleted payload", e);
        }
    }
}
