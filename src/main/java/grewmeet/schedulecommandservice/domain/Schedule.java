package grewmeet.schedulecommandservice.domain;

import jakarta.persistence.Column;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Version;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Schedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private UUID scheduleId;

    private UUID ownerId;

    @Column(nullable = false)
    private String title;

    private String description;

    @Column(nullable = false)
    private LocalDateTime startAt;

    @Column(nullable = false)
    private LocalDateTime endAt;

    // STUDY origin keys (nullable)
    private UUID studyGroupId;
    private UUID meetingId;

    // DATING origin keys (nullable)
    private UUID datingMeetingUuid;

    @Enumerated(EnumType.STRING)
    private ScheduleSource source = ScheduleSource.CUSTOM;

    @Version
    private Long version;

    private Schedule(UUID ownerId,
                     UUID scheduleId,
                     String title,
                     String description,
                     LocalDateTime startAt,
                     LocalDateTime endAt) {
        this.ownerId = ownerId;
        this.scheduleId = scheduleId;
        this.title = title;
        this.description = description;
        this.startAt = startAt;
        this.endAt = endAt;
    }

    public static Schedule createCustom(UUID ownerId,
                                        UUID scheduleId,
                                        String title,
                                        String description,
                                        LocalDateTime startAt,
                                        LocalDateTime endAt) {
        return new Schedule(ownerId, scheduleId, title, description, startAt, endAt);
    }

    public static Schedule createStudy(UUID ownerId,
                                       UUID studyGroupId,
                                       UUID meetingId,
                                       UUID scheduleId,
                                       String title,
                                       String description,
                                       LocalDateTime startAt,
                                       LocalDateTime endAt) {
        Schedule schedule = new Schedule(ownerId, scheduleId, title, description, startAt, endAt);
        schedule.source = ScheduleSource.STUDY;
        schedule.studyGroupId = studyGroupId;
        schedule.meetingId = meetingId;
        return schedule;
    }

    public static Schedule createDating(UUID ownerId,
                                        UUID datingMeetingUuid,
                                        UUID scheduleId,
                                        String title,
                                        String description,
                                        LocalDateTime startAt,
                                        LocalDateTime endAt) {
        Schedule schedule = new Schedule(ownerId, scheduleId, title, description, startAt, endAt);
        schedule.source = ScheduleSource.DATING;
        schedule.datingMeetingUuid = datingMeetingUuid;
        return schedule;
    }

    public void applyPatch(String title,
                           String description,
                           LocalDateTime startAt,
                           LocalDateTime endAt) {
        require(title,startAt,endAt);
        this.title = title;
        this.description = description;
        this.startAt = startAt;
        this.endAt = endAt;
    }

    private void require(String title,
                          LocalDateTime startAt,
                          LocalDateTime endAt) {
        requireStartBeforeEnd(startAt, endAt);
        requireNonBlankTitle(title);
        requireNonZeroDuration(startAt, endAt);
    }

    private void requireStartBeforeEnd(LocalDateTime startAt, LocalDateTime endAt) {
        if(endAt.isBefore(startAt)) {
            throw new IllegalArgumentException("End time must be before start time");
        }
    }

    private void requireNonBlankTitle(String title) {
        if(title == null || title.isBlank()) {
            throw new IllegalArgumentException("Title cannot be blank");
        }
    }

    private void requireNonZeroDuration(LocalDateTime startAt, LocalDateTime endAt) {
        if(startAt.equals(endAt)) {
            throw new IllegalArgumentException("End time must have gap to start time");
        }
    }
}
