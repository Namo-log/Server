package com.namo.spring.application.external.api.schedule.service;

import com.namo.spring.application.external.api.schedule.converter.LocationConverter;
import com.namo.spring.application.external.api.schedule.dto.MeetingScheduleRequest;
import com.namo.spring.application.external.api.schedule.dto.PersonalScheduleRequest;
import com.namo.spring.core.infra.common.properties.ImageUrlProperties;
import com.namo.spring.db.mysql.domains.schedule.entity.Schedule;
import com.namo.spring.db.mysql.domains.schedule.service.ScheduleService;
import com.namo.spring.db.mysql.domains.schedule.type.Location;
import com.namo.spring.db.mysql.domains.schedule.type.Period;
import com.namo.spring.db.mysql.domains.schedule.type.ScheduleType;
import com.namo.spring.db.mysql.domains.user.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import static com.namo.spring.application.external.api.schedule.converter.ScheduleConverter.toBirthdaySchedule;
import static com.namo.spring.application.external.api.schedule.converter.ScheduleConverter.toSchedule;

@Component
@RequiredArgsConstructor
public class ScheduleMaker {
    private static final int PERSONAL_SCHEDULE_TYPE = ScheduleType.PERSONAL.getValue();
    private static final int MEETING_SCHEDULE_TYPE = ScheduleType.MEETING.getValue();
    private static final LocalTime BIRTHDAY_SCHEDULE_START_TIME = LocalTime.of(8,0,0);
    private static final LocalTime BIRTHDAY_SCHEDULE_END_TIME = LocalTime.of(9,0,0);

    private final ScheduleService scheduleService;
    private final ImageUrlProperties imageUrlProperties;

    public Schedule createPersonalSchedule(PersonalScheduleRequest.PostPersonalScheduleDto dto, Period period,
            String nickname) {
        Schedule schedule = toSchedule(dto.getTitle(), period, dto.getLocation(), PERSONAL_SCHEDULE_TYPE, null, null,
                null);
        return scheduleService.createSchedule(schedule);
    }

    public Schedule createMeetingSchedule(MeetingScheduleRequest.PostMeetingScheduleDto dto, Period period) {
        String imageUrl = dto.getImageUrl() != null ? dto.getImageUrl() : imageUrlProperties.getMeeting();
        Schedule schedule = toSchedule(dto.getTitle(), period, dto.getLocation(), MEETING_SCHEDULE_TYPE, imageUrl, 0, "");
        return scheduleService.createSchedule(schedule);
    }

    /**
     * 회원 가입 단계에서
     * 유저의 올해 / 다음 해의 생일 일정을 생성합니다.
     * @param member
     */
    public List<Schedule> createBirthdaySchedules(Member member){
        int currentYear = LocalDate.now().getYear();
        String title = member.getNickname() + "님의 생일";
        Period currentYearPeriod = toPeriodOfBirthdaySchedule(member, currentYear);
        Period nextYearPeriod = toPeriodOfBirthdaySchedule(member, currentYear+1);
        Schedule currentYearBirthday = toBirthdaySchedule(title, currentYearPeriod);
        Schedule nextYearBirthday = toBirthdaySchedule(title, nextYearPeriod);
        return scheduleService.createSchedules(List.of(currentYearBirthday, nextYearBirthday));
    }

    /**
     * 생일 일정의 기본 설정 period를 반환합니다.
     * 시작일시 -> 생일 당일 오전 8시
     * 종료일시 -> 생일 당일 오전 9시
     * @param member
     * @param year
     * @return 생일 일정 기간
     */
    private Period toPeriodOfBirthdaySchedule(Member member, int year){
        LocalDateTime startDateTime =  LocalDateTime.of(member.getBirthday().withYear(year), BIRTHDAY_SCHEDULE_START_TIME);
        LocalDateTime endDatetime =  LocalDateTime.of(member.getBirthday().withYear(year), BIRTHDAY_SCHEDULE_END_TIME);
        return Period.of(startDateTime, endDatetime);
    }
}
