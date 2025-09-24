package grewmeet.schedulecommandservice.service;

import grewmeet.schedulecommandservice.domain.Schedule;
import grewmeet.schedulecommandservice.dto.CreateCustomScheduleRequest;
import grewmeet.schedulecommandservice.dto.DeleteCustomScheduleRequest;
import grewmeet.schedulecommandservice.dto.ScheduleResponse;
import grewmeet.schedulecommandservice.dto.UpdateCustomScheduleRequest;
import grewmeet.schedulecommandservice.repository.ScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ScheduleCommandServiceImpl implements ScheduleCommandService {

    private final ScheduleRepository scheduleRepository;

    @Override
    public ScheduleResponse createCustomSchedule(UUID ownerId, CreateCustomScheduleRequest request) {
        UUID scheduleId = UUID.randomUUID();
        Schedule schedule = Schedule.createCustom(
                ownerId,
                scheduleId,
                request.title(),
                request.description(),
                request.startAt(),
                request.endAt());
        return ScheduleResponse.from(scheduleRepository.save(schedule));
    }

    @Override
    public void patchCustom(UUID ownerId, UpdateCustomScheduleRequest request) {
        Schedule schedule = scheduleRepository
                .findByScheduleIdAndOwnerId(request.scheduleId(), ownerId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        schedule.applyPatch(request.title(), request.description(), request.startAt(), request.endAt());
        scheduleRepository.save(schedule);
    }

    @Override
    public void deleteCustom(UUID ownerId, DeleteCustomScheduleRequest request) {
        Schedule schedule = scheduleRepository
                .findByScheduleIdAndOwnerId(request.scheduleId(), ownerId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        scheduleRepository.delete(schedule);
    }
}
