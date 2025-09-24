package grewmeet.schedulecommandservice.service;

import grewmeet.schedulecommandservice.dto.CreateCustomScheduleRequest;
import grewmeet.schedulecommandservice.dto.DeleteCustomScheduleRequest;
import grewmeet.schedulecommandservice.dto.ScheduleResponse;
import grewmeet.schedulecommandservice.dto.UpdateCustomScheduleRequest;
import grewmeet.schedulecommandservice.repository.ScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ScheduleCommandServiceImpl implements ScheduleCommandService {

    private final ScheduleRepository scheduleRepository;

    @Override
    public ScheduleResponse createCustom(UUID ownerId, CreateCustomScheduleRequest request) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public ScheduleResponse patchCustom(UUID ownerId, UpdateCustomScheduleRequest request) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public void deleteCustom(UUID ownerId, DeleteCustomScheduleRequest request) {
        throw new UnsupportedOperationException("Not implemented yet");
    }
}

