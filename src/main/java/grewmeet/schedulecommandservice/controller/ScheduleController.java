package grewmeet.schedulecommandservice.controller;

import grewmeet.schedulecommandservice.dto.ScheduleResponse;
import grewmeet.schedulecommandservice.service.ScheduleCommandService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import grewmeet.schedulecommandservice.dto.CreateCustomScheduleRequest;
import grewmeet.schedulecommandservice.dto.DeleteCustomScheduleRequest;
import grewmeet.schedulecommandservice.dto.UpdateCustomScheduleRequest;
import jakarta.validation.Valid;
import java.util.UUID;

@RestController
@RequestMapping("/schedules")
@Validated
@RequiredArgsConstructor
public class ScheduleController {

    private final ScheduleCommandService scheduleCommandService;

    @PostMapping("/custom")
    public ResponseEntity<ScheduleResponse> createCustomSchedule(
            @RequestHeader("X-Owner-Id") UUID ownerId,
            @RequestBody @Valid CreateCustomScheduleRequest request) {
        ScheduleResponse created = scheduleCommandService.createCustomSchedule(ownerId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .header("Location", "/schedules/custom/" + created.scheduleId())
                .body(created);
    }

    @PatchMapping("/custom")
    public ResponseEntity<Void> updateCustomSchedule(
            @RequestHeader("X-Owner-Id") UUID ownerId,
            @RequestBody @Valid UpdateCustomScheduleRequest request) {
        scheduleCommandService.patchCustom(ownerId, request);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/custom")
    public ResponseEntity<Void> deleteCustomSchedule(
            @RequestHeader("X-Owner-Id") UUID ownerId,
            @RequestBody @Valid DeleteCustomScheduleRequest request) {
        scheduleCommandService.deleteCustom(ownerId, request);
        return ResponseEntity.noContent().build();
    }
}
