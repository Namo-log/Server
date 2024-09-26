package com.namo.spring.application.external.api.schedule.usecase;

import com.namo.spring.application.external.api.schedule.service.ScheduleManageService;
import com.namo.spring.core.common.code.status.ErrorStatus;
import com.namo.spring.db.mysql.domains.schedule.entity.Schedule;
import com.namo.spring.db.mysql.domains.schedule.exception.ScheduleException;
import com.namo.spring.db.mysql.domains.schedule.service.ScheduleService;
import com.namo.spring.db.mysql.domains.schedule.type.ParticipantRole;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.namo.spring.application.external.api.notification.service.NotificationManageService;
import com.namo.spring.application.external.api.schedule.converter.ScheduleConverter;
import com.namo.spring.application.external.api.schedule.dto.ScheduleRequest;
import com.namo.spring.application.external.api.schedule.dto.ScheduleResponse;
import com.namo.spring.application.external.api.schedule.service.ParticipantManageService;
import com.namo.spring.application.external.api.user.service.MemberManageService;
import com.namo.spring.application.external.global.common.security.authentication.SecurityUserDetails;
import com.namo.spring.db.mysql.domains.schedule.entity.Participant;
import com.namo.spring.db.mysql.domains.user.entity.Member;

import lombok.RequiredArgsConstructor;

import static com.namo.spring.application.external.api.schedule.converter.ScheduleResponseConverter.toScheduleSummaryDto;

@Component
@RequiredArgsConstructor
public class ScheduleUsecase {
    private final MemberManageService memberManageService;
    private final NotificationManageService notificationManageService;
    private final ParticipantManageService participantManageService;
    private final ScheduleManageService scheduleManageService;

    @Transactional(readOnly = true)
    public ScheduleResponse.ScheduleSummaryDto getScheduleSummary(Long memberId, Long scheduleId) {
        Participant myScheduleParticipant = participantManageService.getParticipantByMemberAndSchedule(memberId, scheduleId);
        return toScheduleSummaryDto(myScheduleParticipant);
    }

    @Transactional
    public void updateOrCreateScheduleReminder(ScheduleRequest.PutScheduleReminderDto dto, Long scheduleId,
            SecurityUserDetails memberInfo) {
        Member member = memberManageService.getActiveMember(memberInfo.getUserId());
        notificationManageService.updateOrCreateScheduleReminderNotification(scheduleId, member,
                dto.getReminderTrigger());
    }

    @Transactional
    public void deleteSchedule(Long scheduleId, SecurityUserDetails memberInfo) {
        Schedule schedule= scheduleManageService.getSchedule(scheduleId);
        scheduleManageService.deleteSchedule(schedule, memberInfo.getUserId());
    }
}
