package dev.glabay.scheduling.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * @author Glabay | Glabay-Studios
 * @project midnight-technician
 * @social Discord: Glabay
 * @since 2025-11-05
 */
@Document(collection = "time_records")
public class TimeRecord {
    @Id
    private String id;
    private String employeeId;
    private LocalDate date;
    private LocalDateTime clockIn;
    private LocalDateTime clockOut;
    private Duration totalTimeWorked;
    private boolean manualAdjustment;

    public void calculateTotalWorked() {
        if (clockIn != null && clockOut != null)
            this.totalTimeWorked = Duration.between(clockIn, clockOut);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public LocalDateTime getClockIn() {
        return clockIn;
    }

    public void setClockIn(LocalDateTime clockIn) {
        this.clockIn = clockIn;
    }

    public LocalDateTime getClockOut() {
        return clockOut;
    }

    public void setClockOut(LocalDateTime clockOut) {
        this.clockOut = clockOut;
    }

    public Duration getTotalTimeWorked() {
        return totalTimeWorked;
    }

    public void setTotalTimeWorked(Duration totalTimeWorked) {
        this.totalTimeWorked = totalTimeWorked;
    }

    public boolean isManualAdjustment() {
        return manualAdjustment;
    }

    public void setManualAdjustment(boolean manualAdjustment) {
        this.manualAdjustment = manualAdjustment;
    }
}
