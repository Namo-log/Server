package com.namo.spring.application.external.api.schedule.controller;

import static com.namo.spring.core.common.code.status.ErrorStatus.*;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.namo.spring.application.external.api.schedule.dto.PersonalScheduleRequest;
import com.namo.spring.application.external.api.schedule.dto.PersonalScheduleResponse;
import com.namo.spring.application.external.api.schedule.usecase.PersonalScheduleUsecase;
import com.namo.spring.application.external.global.annotation.swagger.ApiErrorCodes;
import com.namo.spring.application.external.global.common.security.authentication.SecurityUserDetails;
import com.namo.spring.core.common.response.ResponseDto;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Tag(name = "개인 일정", description = "개인 일정 API")
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v2/schedules")
public class PersonalScheduleController  {
    private final PersonalScheduleUsecase personalScheduleUsecase;

    @Operation(summary = "개인 일정 생성", description = "개인 일정을 생성합니다. 요청 성공 시 개인 일정 ID를 전송합니다.")
    @ApiErrorCodes(value = {
            NOT_FOUND_USER_FAILURE,
            NOT_FOUND_CATEGORY_FAILURE,
            INVALID_DATE})
    @PostMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseDto<Long> createPersonalSchedule(
            @Valid @RequestBody PersonalScheduleRequest.PostPersonalScheduleDto dto,
            @AuthenticationPrincipal SecurityUserDetails member) throws JsonProcessingException {
        return ResponseDto.onSuccess(personalScheduleUsecase.createPersonalSchedule(dto, member));
    }

    @Operation(summary = "개인 월간 일정 조회", description = "개인 월간 일정을 조회합니다.")
    @ApiErrorCodes(value = {
            INVALID_FORMAT_FAILURE
    })
    @GetMapping("/calendar")
    public ResponseDto<List<PersonalScheduleResponse.GetMonthlyScheduleDto>> getMyMonthlySchedules(
            @RequestParam Integer year,
            @RequestParam Integer month,
            @AuthenticationPrincipal SecurityUserDetails member) {
        return ResponseDto.onSuccess(personalScheduleUsecase.getMyMonthlySchedules(year, month, member));
    }

    @Operation(summary = "친구 월간 일정 조회", description = "친구의 월간 일정을 조회합니다.")
    @ApiErrorCodes(value = {NOT_FRIENDSHIP_MEMBER})
    @GetMapping("/calendar/friends")
    public ResponseDto<List<PersonalScheduleResponse.GetFriendMonthlyScheduleDto>> getFriendMonthlySchedules(
            @RequestParam Integer year,
            @RequestParam Integer month,
            @RequestParam Long memberId,
            @AuthenticationPrincipal SecurityUserDetails member) {
        return ResponseDto.onSuccess(personalScheduleUsecase.getFriendMonthlySchedules(year, month, memberId, member));
    }

    @Operation(summary = "개인 일정 내용 수정", description = "개인 일정 내용을 수정합니다.")
    @ApiErrorCodes(value = {
            INVALID_DATE,
            NOT_SCHEDULE_OWNER,
            NOT_FOUND_SCHEDULE_FAILURE,
            NOT_PERSONAL_SCHEDULE
    })
    @PatchMapping("/{scheduleId}")
    public ResponseDto<String> updatePersonalSchedules(@PathVariable Long scheduleId,
            @Valid @RequestBody PersonalScheduleRequest.PatchPersonalScheduleDto dto,
            @AuthenticationPrincipal SecurityUserDetails member) {
        personalScheduleUsecase.updatePersonalSchedule(dto, scheduleId, member);
        return ResponseDto.onSuccess("일정 수정 성공");
    }
}
