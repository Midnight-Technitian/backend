package dev.glabay.scheduling.services;

import dev.glabay.dtos.ScheduleDto;
import dev.glabay.dtos.Shift;
import dev.glabay.logging.MidnightLogger;
import dev.glabay.scheduling.models.Schedule;
import dev.glabay.scheduling.repos.ScheduleRepository;
import dev.glabay.services.SequenceGeneratorService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author Glabay | Glabay-Studios
 * @project midnight-technician
 * @social Discord: Glabay
 * @since 2025-11-11
 */
@Service
@RequiredArgsConstructor
public class ScheduleService {
    private final Logger logger = MidnightLogger.getLogger(ScheduleService.class);
    private final ScheduleRepository scheduleRepository;
    private final SequenceGeneratorService sequenceGeneratorService;

    public List<ScheduleDto> getAllSchedules() {
        return scheduleRepository.findAll().stream()
            .map(this::toDto)
            .toList();
    }

    public List<ScheduleDto> getSchedulesByWeek(LocalDate weekStart) {
        return scheduleRepository.findByWeekStartDate(weekStart).stream()
            .map(this::toDto)
            .toList();
    }

    public Optional<ScheduleDto> getScheduleByEmployeeId(String employeeId) {
        var weekStart = getCurrentWeekStart();
        return scheduleRepository.findByEmployeeIdAndWeekStartDate(employeeId, weekStart)
            .map(this::toDto);
    }

    @Transactional
    public ScheduleDto addOrUpdateShift(String employeeId, Shift shift) {
        var weekStart = getCurrentWeekStart();

        var schedule = scheduleRepository.findByEmployeeIdAndWeekStartDate(employeeId, weekStart)
            .orElseGet(() -> new Schedule(
                sequenceGeneratorService.getNextSequence("midnight_schedule_sequence"),
                employeeId,
                weekStart,
                new ArrayList<>()
            ));
        schedule.shifts().removeIf(s -> s.day().equals(shift.day()));
        schedule.shifts().add(shift);
        var saved = scheduleRepository.save(schedule);
        logger.info("Added/Updated shift for employee {} on {}", employeeId, shift.day());
        return toDto(saved);
    }

    @Transactional
    public void removeShift(String employeeId, DayOfWeek day) {
        var weekStart = getCurrentWeekStart();
        scheduleRepository.findByEmployeeIdAndWeekStartDate(employeeId, weekStart)
            .ifPresent(schedule -> {
                schedule.shifts().removeIf(shift -> shift.day().equals(day));

                if (schedule.shifts().isEmpty()) {
                    scheduleRepository.delete(schedule);
                    logger.info("Deleted empty schedule for employee {}", employeeId);
                } else {
                    scheduleRepository.save(schedule);
                    logger.info("Removed shift for employee {} on {}", employeeId, day);
                }
            });
    }

    @Transactional
    public ScheduleDto addOrUpdateShift(String employeeId, Shift shift, LocalDate weekStart) {
        // Find existing schedule or create new one
        var schedule = scheduleRepository.findByEmployeeIdAndWeekStartDate(employeeId, weekStart)
            .orElseGet(() -> new Schedule(
                sequenceGeneratorService.getNextSequence("midnight_schedule_sequence"),
                employeeId,
                weekStart,
                new ArrayList<>()
            ));
        schedule.shifts().removeIf(s -> s.day().equals(shift.day()));
        schedule.shifts().add(shift);

        // Save and return
        var saved = scheduleRepository.save(schedule);
        logger.info("Added/Updated shift for employee {} on {} for week {}", employeeId, shift.day(), weekStart);
        return toDto(saved);
    }

    @Transactional
    public void removeShift(String employeeId, DayOfWeek day, LocalDate weekStart) {
        scheduleRepository.findByEmployeeIdAndWeekStartDate(employeeId, weekStart)
            .ifPresent(schedule -> {
                schedule.shifts().removeIf(shift -> shift.day().equals(day));

                if (schedule.shifts().isEmpty()) {
                    scheduleRepository.delete(schedule);
                    logger.info("Deleted empty schedule for employee {} for week {}", employeeId, weekStart);
                } else {
                    scheduleRepository.save(schedule);
                    logger.info("Removed shift for employee {} on {} for week {}", employeeId, day, weekStart);
                }
            });
    }

    private LocalDate getCurrentWeekStart() {
        return LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
    }

    private ScheduleDto toDto(Schedule schedule) {
        return new ScheduleDto(
            schedule.id(),
            schedule.employeeId(),
            schedule.weekStartDate(),
            schedule.shifts()
        );
    }
}