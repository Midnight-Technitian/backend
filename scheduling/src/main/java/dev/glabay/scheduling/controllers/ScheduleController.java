package dev.glabay.scheduling.controllers;

import dev.glabay.dtos.ScheduleDto;
import dev.glabay.dtos.Shift;
import dev.glabay.logging.MidnightLogger;
import dev.glabay.scheduling.services.ScheduleService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

/**
 * @author Glabay | Glabay-Studios
 * @project midnight-technician
 * @social Discord: Glabay
 * @since 2025-11-11
 */
@RestController
@RequestMapping("/api/v1/schedules")
@RequiredArgsConstructor
public class ScheduleController {
    private final Logger logger = MidnightLogger.getLogger(ScheduleController.class);
    private final ScheduleService scheduleService;

    @GetMapping("/all")
    public ResponseEntity<List<ScheduleDto>> getAllSchedules() {
        try {
            var schedules = scheduleService.getAllSchedules();
            return ResponseEntity.ok(schedules);
        }
        catch (Exception e) {
            logger.error("Error fetching all schedules: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/week")
    public ResponseEntity<List<ScheduleDto>> getSchedulesByWeek(
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate weekStart) {
        try {
            var schedules = scheduleService.getSchedulesByWeek(weekStart);
            return ResponseEntity.ok(schedules);
        }
        catch (Exception e) {
            logger.error("Error fetching schedules for week {}: {}", weekStart, e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/{employeeId}")
    public ResponseEntity<ScheduleDto> getScheduleByEmployee(@PathVariable String employeeId) {
        try {
            var schedule = scheduleService.getScheduleByEmployeeId(employeeId);
            return schedule.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
        }
        catch (Exception e) {
            logger.error("Error fetching schedule for employee {}: {}", employeeId, e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/shift")
    public ResponseEntity<ScheduleDto> addOrUpdateShift(
        @RequestParam String employeeId,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate weekStart,
        @RequestBody Shift shift) {
        try {
            var schedule = scheduleService.addOrUpdateShift(employeeId, shift, weekStart);
            return ResponseEntity.ok(schedule);
        }
        catch (Exception e) {
            logger.error("Error adding/updating shift for employee {}: {}", employeeId, e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @DeleteMapping("/shift")
    public ResponseEntity<Void> deleteShift(
        @RequestParam String employeeId,
        @RequestParam DayOfWeek day,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate weekStart) {
        try {
            scheduleService.removeShift(employeeId, day, weekStart);
            return ResponseEntity.noContent().build();
        }
        catch (Exception e) {
            logger.error("Error deleting shift for employee {} on {}: {}", employeeId, day, e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
}