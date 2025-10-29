package grewmeet.schedulecommandservice.event.publisher;

import grewmeet.schedulecommandservice.domain.Schedule;
import grewmeet.schedulecommandservice.event.schema.ScheduleCreated;
import grewmeet.schedulecommandservice.event.schema.ScheduleDeleted;
import grewmeet.schedulecommandservice.event.schema.ScheduleUpdated;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ScheduleEventPublisherImpl implements ScheduleEventPublisher {

    private static final String TOPIC = "schedule.events";

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Override
    public void publishCreated(Schedule schedule) {
        ScheduleCreated payload = new ScheduleCreated(
                schedule.getScheduleId(),
                schedule.getOwnerId(),
                schedule.getTitle(),
                schedule.getDescription(),
                schedule.getStartAt(),
                schedule.getEndAt(),
                schedule.getSource(),
                schedule.getStudyGroupId(),
                schedule.getMeetingId(),
                getVersionOrDefault(schedule.getVersion()),
                LocalDateTime.now()
        );
        send(schedule.getScheduleId(), payload, "CREATED");
    }

    @Override
    public void publishUpdated(Schedule schedule) {
        ScheduleUpdated payload = new ScheduleUpdated(
                schedule.getScheduleId(),
                schedule.getOwnerId(),
                schedule.getTitle(),
                schedule.getDescription(),
                schedule.getStartAt(),
                schedule.getEndAt(),
                schedule.getSource(),
                schedule.getStudyGroupId(),
                schedule.getMeetingId(),
                getVersionOrDefault(schedule.getVersion()),
                LocalDateTime.now()
        );
        send(schedule.getScheduleId(), payload, "UPDATED");
    }

    @Override
    public void publishDeleted(UUID scheduleId, UUID ownerId) {
        ScheduleDeleted payload = new ScheduleDeleted(
                scheduleId,
                ownerId,
                grewmeet.schedulecommandservice.domain.ScheduleSource.CUSTOM,
                0L,
                LocalDateTime.now()
        );
        send(scheduleId, payload, "DELETED");
    }

    private long getVersionOrDefault(Long version) {
        if (version != null) {
            return version;
        }
        return 0L;
    }

    private void send(UUID scheduleId, Object payload, String tag) {
        String key = scheduleId.toString();
        kafkaTemplate.send(TOPIC, key, payload).whenComplete((result, ex) -> {
            if (ex == null) {
                log.info("Schedule event published [{}]: topic={}, key={}, partition={}, offset={}",
                        tag,
                        TOPIC,
                        key,
                        result.getRecordMetadata().partition(),
                        result.getRecordMetadata().offset());
                return;
            }
            log.error("Schedule event publish failed [{}]: topic={}, key={}", tag, TOPIC, key, ex);
        });
    }
}
