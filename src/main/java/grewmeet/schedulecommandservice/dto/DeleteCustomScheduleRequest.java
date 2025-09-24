package grewmeet.schedulecommandservice.dto;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record DeleteCustomScheduleRequest(
        @NotNull UUID scheduleId
) {}
