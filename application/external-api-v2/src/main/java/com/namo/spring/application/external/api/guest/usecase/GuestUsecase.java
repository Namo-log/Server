package com.namo.spring.application.external.api.guest.usecase;

import static com.namo.spring.application.external.api.guest.converter.GuestMeetingResponseConverter.toGetMeetingScheduleInfoDto;
import static com.namo.spring.application.external.api.guest.converter.GuestParticipantResponseConverter.*;

import com.namo.spring.application.external.api.guest.dto.GuestMeetingResponse;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.namo.spring.application.external.api.guest.dto.GuestParticipantRequest;
import com.namo.spring.application.external.api.guest.dto.GuestParticipantResponse;
import com.namo.spring.application.external.api.guest.service.GuestManageService;
import com.namo.spring.application.external.api.schedule.service.ScheduleManageService;
import com.namo.spring.db.mysql.domains.schedule.entity.Schedule;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Component
public class GuestUsecase {
    private final ScheduleManageService scheduleManageService;
    private final GuestManageService guestManageService;

    @Transactional
    public GuestParticipantResponse.PostGuestParticipantInfoDto createOrValidateGuest(
            GuestParticipantRequest.PostGuestParticipantDto dto, String code) {
        Long scheduleId = guestManageService.decodeInviteCode(code);
        Schedule schedule = scheduleManageService.getMeetingSchedule(scheduleId);
        return toPostGuestParticipantDto(guestManageService.createOrValidateGuest(dto, schedule, code));
    }

    @Transactional(readOnly = true)
    public GuestMeetingResponse.GetMeetingScheduleInfoDto getScheduleInfo(String code){
        Long scheduleId = guestManageService.decodeInviteCode(code);
        Schedule schedule = scheduleManageService.getMeetingSchedule(scheduleId);
        return toGetMeetingScheduleInfoDto(schedule);
    }
}
