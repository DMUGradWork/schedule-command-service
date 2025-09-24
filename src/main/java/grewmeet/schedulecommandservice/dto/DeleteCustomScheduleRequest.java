package grewmeet.schedulecommandservice.dto;

import java.util.UUID;

public record DeleteCustomScheduleRequest(
        UUID scheduleId
) {}

