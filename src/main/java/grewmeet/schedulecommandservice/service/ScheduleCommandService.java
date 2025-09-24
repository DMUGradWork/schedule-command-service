package grewmeet.schedulecommandservice.service;

import grewmeet.schedulecommandservice.dto.CreateCustomScheduleRequest;
import grewmeet.schedulecommandservice.dto.DeleteCustomScheduleRequest;
import grewmeet.schedulecommandservice.dto.ScheduleResponse;
import grewmeet.schedulecommandservice.dto.UpdateCustomScheduleRequest;

import java.util.UUID;

public interface ScheduleCommandService {
    ScheduleResponse createCustomSchedule(UUID ownerId, CreateCustomScheduleRequest request);

    ScheduleResponse patchCustom(UUID ownerId, UpdateCustomScheduleRequest request);

    void deleteCustom(UUID ownerId, DeleteCustomScheduleRequest request);
}

