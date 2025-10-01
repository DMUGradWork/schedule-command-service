package grewmeet.schedulecommandservice.repository;

import grewmeet.schedulecommandservice.domain.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
    Optional<Schedule> findByUuid(UUID uuid);
    Optional<Schedule> findByScheduleIdAndOwnerId(UUID scheduleId, UUID ownerId);
    List<Schedule> findAllByStudyGroupIdAndMeetingId(UUID studyGroupId, UUID meetingId);
}
