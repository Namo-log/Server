package com.namo.spring.application.external.api.schedule.usecase;

import static com.namo.spring.application.external.api.schedule.converter.PersonalScheduleResponseConverter.*;
import static com.namo.spring.application.external.global.utils.SchedulePeriodValidationUtils.*;

import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.namo.spring.application.external.api.notification.service.NotificationManageService;
import com.namo.spring.application.external.api.schedule.dto.PersonalScheduleRequest;
import com.namo.spring.application.external.api.schedule.dto.PersonalScheduleResponse;
import com.namo.spring.application.external.api.schedule.service.ScheduleManageService;
import com.namo.spring.application.external.api.user.service.MemberManageService;
import com.namo.spring.application.external.global.common.security.authentication.SecurityUserDetails;
import com.namo.spring.db.mysql.domains.notification.dto.ScheduleNotificationQuery;
import com.namo.spring.db.mysql.domains.schedule.entity.Participant;
import com.namo.spring.db.mysql.domains.schedule.entity.Schedule;
import com.namo.spring.db.mysql.domains.user.entity.Member;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Component
public class PersonalScheduleUsecase {
    private final MemberManageService memberManageService;
    private final ScheduleManageService scheduleManageService;
    private final NotificationManageService notificationManageService;

    @Transactional
    public Long createPersonalSchedule(PersonalScheduleRequest.PostPersonalScheduleDto dto,
            SecurityUserDetails memberInfo) throws JsonProcessingException {
        Member member = memberManageService.getActiveMember(memberInfo.getUserId());
        Schedule schedule = scheduleManageService.createPersonalSchedule(dto, member);
        if (!dto.getReminderTrigger().isEmpty()) {
            notificationManageService.createScheduleReminderNotification(schedule, member, dto.getReminderTrigger());
        }
        return schedule.getId();
    }

    @Transactional(readOnly = true)
    public List<PersonalScheduleResponse.GetMonthlyScheduleDto> getMyMonthlySchedules(int year, int month,
            SecurityUserDetails memberInfo) {
        List<Participant> scheduleInfo = scheduleManageService.getMyMonthlySchedules(memberInfo.getUserId(),
                getExtendedPeriod(year, month));
        List<ScheduleNotificationQuery> scheduleNotifications = notificationManageService.getScheduleNotifications(
                memberInfo.getUserId(), scheduleInfo);
        return toGetMonthlyScheduleDtos(scheduleInfo, scheduleNotifications);
    }

    @Transactional
    public void updatePersonalSchedule(PersonalScheduleRequest.PatchPersonalScheduleDto patchPersonalScheduleDto,
            Long scheduleId, SecurityUserDetails memberInfo) {
        Schedule schedule = scheduleManageService.getPersonalSchedule(scheduleId);
        scheduleManageService.updatePersonalSchedule(patchPersonalScheduleDto, schedule, memberInfo.getUserId());
    }

    @Transactional(readOnly = true)
    public List<PersonalScheduleResponse.GetFriendMonthlyScheduleDto> getFriendMonthlySchedules(int year, int month,
            Long targetMemberId, SecurityUserDetails memberInfo) {
        Member targetMember = memberManageService.getActiveMember(targetMemberId);
        return toGetFriendMonthlyScheduleDtos(
                scheduleManageService.getMemberMonthlySchedules(targetMember.getId(), memberInfo.getUserId(),
                        getExtendedPeriod(year, month)));
    }
}
