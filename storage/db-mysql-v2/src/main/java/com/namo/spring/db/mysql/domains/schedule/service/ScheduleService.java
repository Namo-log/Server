package com.namo.spring.db.mysql.domains.schedule.service;

import java.util.List;
import java.util.Optional;

import org.springframework.transaction.annotation.Transactional;

import com.namo.spring.core.common.annotation.DomainService;
import com.namo.spring.db.mysql.domains.schedule.entity.Schedule;
import com.namo.spring.db.mysql.domains.schedule.repository.ScheduleRepository;

import lombok.RequiredArgsConstructor;

@DomainService
@RequiredArgsConstructor
public class ScheduleService {
    private final ScheduleRepository scheduleRepository;

    @Transactional
    public Schedule createSchedule(Schedule schedule) {
        return scheduleRepository.save(schedule);
    }

    @Transactional
    public List<Schedule> createSchedules(List<Schedule> schedule) {
        return scheduleRepository.saveAll(schedule);
    }

    @Transactional(readOnly = true)
    public Optional<Schedule> readSchedule(Long id) {
        return scheduleRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<Schedule> readSchedulesById(List<Long> ids) {
        return scheduleRepository.findSchedulesByIdIn(ids);
    }

    @Transactional
    public void deleteSchedule(Long id) {
        scheduleRepository.deleteById(id);
    }
}
