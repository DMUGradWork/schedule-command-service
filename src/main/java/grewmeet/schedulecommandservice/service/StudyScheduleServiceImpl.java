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
public class StudyScheduleServiceImpl implements StudyScheduleService {

    private static final Logger log = LoggerFactory.getLogger(StudyScheduleServiceImpl.class);

    private final ScheduleRepository scheduleRepository;
    private final ScheduleEventPublisher scheduleEventPublisher;

    @Override
    public void register(UUID userId,
                         UUID studyGroupId,
                         UUID meetingId,
                         String meetingName,
                         String description,
                         LocalDateTime startAt,
                         LocalDateTime endAt) {
        UUID scheduleId = deterministicScheduleId(meetingId, userId);
        UUID ownerId = userId;

        Optional<Schedule> existing = scheduleRepository.findByScheduleIdAndOwnerId(scheduleId, ownerId);
        if (existing.isPresent()) {
            Schedule schedule = existing.get();
            schedule.applyPatch(meetingName, description, startAt, endAt);
            Schedule saved = scheduleRepository.save(schedule);
            scheduleEventPublisher.publishUpdated(saved);
            log.info("Study register -> Updated schedule: scheduleId={}, ownerId={}", scheduleId, ownerId);
            return;
        }

        Schedule created = Schedule.createStudy(ownerId, studyGroupId, meetingId, scheduleId, meetingName, description, startAt, endAt);
        Schedule saved = scheduleRepository.save(created);
        scheduleEventPublisher.publishCreated(saved);
        log.info("Study register -> Created schedule: scheduleId={}, ownerId={}", scheduleId, ownerId);
    }

    @Override
    public void update(UUID userId,
                       UUID meetingId,
                       String newMeetingName,
                       String newDescription,
                       LocalDateTime newStartAt,
                       LocalDateTime newEndAt) {
        UUID scheduleId = deterministicScheduleId(meetingId, userId);
        UUID ownerId = userId;

        Optional<Schedule> existing = scheduleRepository.findByScheduleIdAndOwnerId(scheduleId, ownerId);
        if (existing.isEmpty()) {
            log.info("Study update -> Schedule not found: scheduleId={}, ownerId={}", scheduleId, ownerId);
            return;
        }

        Schedule schedule = existing.get();
        schedule.applyPatch(newMeetingName, newDescription, newStartAt, newEndAt);
        Schedule saved = scheduleRepository.save(schedule);
        scheduleEventPublisher.publishUpdated(saved);
        log.info("Study update -> Updated schedule: scheduleId={}, ownerId={}", scheduleId, ownerId);
    }

    @Override
    public void delete(UUID userId, UUID meetingId) {
        UUID scheduleId = deterministicScheduleId(meetingId, userId);
        UUID ownerId = userId;

        Optional<Schedule> existing = scheduleRepository.findByScheduleIdAndOwnerId(scheduleId, ownerId);
        if (existing.isEmpty()) {
            log.info("Study delete -> Schedule not found: scheduleId={}, ownerId={}", scheduleId, ownerId);
            return;
        }

        Schedule schedule = existing.get();
        scheduleRepository.delete(schedule);
        scheduleEventPublisher.publishDeleted(scheduleId, ownerId);
        log.info("Study delete -> Deleted schedule: scheduleId={}, ownerId={}", scheduleId, ownerId);
    }

    @Override
    public void reschedule(UUID studyGroupId,
                           UUID meetingId,
                           String newMeetingName,
                           String newDescription,
                           LocalDateTime newStartAt,
                           LocalDateTime newEndAt) {
        List<Schedule> schedules = scheduleRepository.findAllByStudyGroupIdAndMeetingId(studyGroupId, meetingId);
        if (schedules.isEmpty()) {
            log.info("Study reschedule -> No schedules found: studyGroupId={}, meetingId={}", studyGroupId, meetingId);
            return;
        }

        for (Schedule schedule : schedules) {
            schedule.applyPatch(newMeetingName, newDescription, newStartAt, newEndAt);
            Schedule saved = scheduleRepository.save(schedule);
            scheduleEventPublisher.publishUpdated(saved);
            log.info("Study reschedule -> Updated schedule: scheduleId={}, ownerId={}", saved.getScheduleId(), saved.getOwnerId());
        }
    }

    @Override
    public void cancelMeeting(UUID studyGroupId, UUID meetingId) {
        List<Schedule> schedules = scheduleRepository.findAllByStudyGroupIdAndMeetingId(studyGroupId, meetingId);
        if (schedules.isEmpty()) {
            log.info("Study cancel meeting -> No schedules found: studyGroupId={}, meetingId={}", studyGroupId, meetingId);
            return;
        }

        for (Schedule schedule : schedules) {
            UUID scheduleId = schedule.getScheduleId();
            UUID ownerId = schedule.getOwnerId();
            scheduleRepository.delete(schedule);
            scheduleEventPublisher.publishDeleted(scheduleId, ownerId);
            log.info("Study cancel meeting -> Deleted schedule: scheduleId={}, ownerId={}", scheduleId, ownerId);
        }
    }

    private UUID deterministicScheduleId(UUID meetingId, UUID userId) {
        String key = "study:" + meetingId + ":" + userId;
        return UUID.nameUUIDFromBytes(key.getBytes(StandardCharsets.UTF_8));
    }
}
