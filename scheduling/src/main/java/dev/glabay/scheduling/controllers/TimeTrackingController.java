package dev.glabay.scheduling.controllers;

import dev.glabay.scheduling.services.TimeTrackingService;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Glabay | Glabay-Studios
 * @project midnight-technician
 * @social Discord: Glabay
 * @since 2025-11-05
 */
@NullMarked
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/schedule")
public class TimeTrackingController {
    private final TimeTrackingService timeTrackingService;

    @PostMapping("/clock-in")
    private ResponseEntity<Void> handleClockIn(@RequestParam("employeeId") String employeeId) {
        // TODO: Verification that the employeeId is valid
        var clockedIn = timeTrackingService.clockIn(employeeId);
        if (!clockedIn)
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        return ResponseEntity.ok().build();
    }

    @PostMapping("/clock-out")
    private ResponseEntity<Void> handleClockOut(@RequestParam("employeeId") String employeeId) {
        // TODO: Verification that the employeeId is valid
        var clockedOut = timeTrackingService.clockOut(employeeId);
        if (!clockedOut)
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        return ResponseEntity.ok().build();
    }
}
