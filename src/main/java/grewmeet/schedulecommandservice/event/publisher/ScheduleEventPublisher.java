package grewmeet.schedulecommandservice.event.publisher;

import grewmeet.schedulecommandservice.domain.Schedule;
import java.util.UUID;

public interface ScheduleEventPublisher {

    void publishCreated(Schedule schedule);

    void publishUpdated(Schedule schedule);

    void publishDeleted(UUID scheduleId, UUID ownerId);
}

