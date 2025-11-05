package grewmeet.schedulecommandservice.service;

import grewmeet.schedulecommandservice.domain.Schedule;
import grewmeet.schedulecommandservice.event.publisher.ScheduleEventPublisher;
import grewmeet.schedulecommandservice.repository.ScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DatingScheduleServiceImpl implements DatingScheduleService {

    private static final Logger log = LoggerFactory.getLogger(DatingScheduleServiceImpl.class);
    private static final int DATING_MEETING_DURATION_HOURS = 3;

    private final ScheduleRepository scheduleRepository;
    private final ScheduleEventPublisher scheduleEventPublisher;

    @Override
    public void joinParticipant(UUID authUserId,
                                UUID meetingUuid,
                                String meetingTitle,
                                LocalDateTime meetingDateTime) {
        UUID scheduleId = deterministicScheduleId(meetingUuid, authUserId);
        UUID ownerId = authUserId;
        LocalDateTime endAt = meetingDateTime.plusHours(DATING_MEETING_DURATION_HOURS);

        Optional<Schedule> existing = scheduleRepository.findByScheduleIdAndOwnerId(scheduleId, ownerId);
        if (existing.isPresent()) {
            Schedule schedule = existing.get();
            schedule.applyPatch(meetingTitle, null, meetingDateTime, endAt);
            Schedule saved = scheduleRepository.save(schedule);
            scheduleEventPublisher.publishUpdated(saved);
            log.info("Dating joinParticipant -> Updated schedule: scheduleId={}, ownerId={}", scheduleId, ownerId);
            return;
        }

        Schedule created = Schedule.createDating(
                ownerId,
                meetingUuid,
                scheduleId,
                meetingTitle,
                null,
                meetingDateTime,
                endAt
        );
        Schedule saved = scheduleRepository.save(created);
        scheduleEventPublisher.publishCreated(saved);
        log.info("Dating joinParticipant -> Created schedule: scheduleId={}, ownerId={}", scheduleId, ownerId);
    }

    @Override
    public void leaveParticipant(UUID authUserId, UUID meetingUuid) {
        UUID scheduleId = deterministicScheduleId(meetingUuid, authUserId);
        UUID ownerId = authUserId;

        Optional<Schedule> existing = scheduleRepository.findByScheduleIdAndOwnerId(scheduleId, ownerId);
        if (existing.isEmpty()) {
            log.info("Dating leaveParticipant -> Schedule not found: scheduleId={}, ownerId={}", scheduleId, ownerId);
            return;
        }

        Schedule schedule = existing.get();
        scheduleRepository.delete(schedule);
        scheduleEventPublisher.publishDeleted(scheduleId, ownerId);
        log.info("Dating leaveParticipant -> Deleted schedule: scheduleId={}, ownerId={}", scheduleId, ownerId);
    }

    @Override
    public void updateMeeting(UUID meetingUuid,
                              String title,
                              String description,
                              LocalDateTime meetingDateTime) {
        List<Schedule> schedules = scheduleRepository.findAllByDatingMeetingUuid(meetingUuid);
        if (schedules.isEmpty()) {
            log.info("Dating updateMeeting -> No schedules found: meetingUuid={}", meetingUuid);
            return;
        }

        LocalDateTime endAt = meetingDateTime.plusHours(DATING_MEETING_DURATION_HOURS);

        for (Schedule schedule : schedules) {
            schedule.applyPatch(title, description, meetingDateTime, endAt);
            Schedule saved = scheduleRepository.save(schedule);
            scheduleEventPublisher.publishUpdated(saved);
            log.info("Dating updateMeeting -> Updated schedule: scheduleId={}, ownerId={}", saved.getScheduleId(), saved.getOwnerId());
        }
    }

    @Override
    public void deleteMeeting(UUID meetingUuid) {
        List<Schedule> schedules = scheduleRepository.findAllByDatingMeetingUuid(meetingUuid);
        if (schedules.isEmpty()) {
            log.info("Dating deleteMeeting -> No schedules found: meetingUuid={}", meetingUuid);
            return;
        }

        for (Schedule schedule : schedules) {
            UUID scheduleId = schedule.getScheduleId();
            UUID ownerId = schedule.getOwnerId();
            scheduleRepository.delete(schedule);
            scheduleEventPublisher.publishDeleted(scheduleId, ownerId);
            log.info("Dating deleteMeeting -> Deleted schedule: scheduleId={}, ownerId={}", scheduleId, ownerId);
        }
    }

    private UUID deterministicScheduleId(UUID meetingUuid, UUID userId) {
        String key = "dating:" + meetingUuid + ":" + userId;
        return UUID.nameUUIDFromBytes(key.getBytes(StandardCharsets.UTF_8));
    }
}
