package grewmeet.schedulecommandservice.domain;

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

    private UUID scheduleId;

    private UUID ownerId;

    private String title;

    private String description;

    private LocalDateTime startAt;

    private LocalDateTime endAt;

    private boolean allDay;

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
}
