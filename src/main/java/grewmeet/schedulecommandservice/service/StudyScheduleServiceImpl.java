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

        Schedule created = Schedule.createCustom(ownerId, scheduleId, meetingName, description, startAt, endAt);
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

    private UUID deterministicScheduleId(UUID meetingId, UUID userId) {
        String key = "study:" + meetingId + ":" + userId;
        return UUID.nameUUIDFromBytes(key.getBytes(StandardCharsets.UTF_8));
    }
}
