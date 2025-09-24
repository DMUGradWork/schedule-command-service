package grewmeet.schedulecommandservice.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import grewmeet.schedulecommandservice.dto.CreateCustomScheduleRequest;
import grewmeet.schedulecommandservice.dto.DeleteCustomScheduleRequest;
import grewmeet.schedulecommandservice.dto.UpdateCustomScheduleRequest;

@RestController
@RequestMapping("/schedules")
@Validated
public class ScheduleController {

    @PostMapping("/custom")
    public ResponseEntity<Void> createCustomSchedule(@RequestBody CreateCustomScheduleRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PatchMapping("/custom")
    public ResponseEntity<Void> updateCustomSchedule(@RequestBody UpdateCustomScheduleRequest request) {
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/custom")
    public ResponseEntity<Void> deleteCustomSchedule(@RequestBody DeleteCustomScheduleRequest request) {
        return ResponseEntity.noContent().build();
    }
}
