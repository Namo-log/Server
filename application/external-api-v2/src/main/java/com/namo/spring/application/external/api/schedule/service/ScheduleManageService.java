package com.namo.spring.application.external.api.schedule.service;

import static com.namo.spring.application.external.global.utils.MeetingParticipantValidationUtils.*;
import static com.namo.spring.application.external.global.utils.PeriodValidationUtils.*;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import com.namo.spring.application.external.api.category.service.CategoryManageService;
import com.namo.spring.db.mysql.domains.category.entity.Category;
import com.namo.spring.db.mysql.domains.schedule.dto.ScheduleSummaryQuery;
import org.springframework.stereotype.Service;

import com.namo.spring.application.external.api.schedule.converter.LocationConverter;
import com.namo.spring.application.external.api.schedule.dto.MeetingScheduleRequest;
import com.namo.spring.application.external.api.schedule.dto.PersonalScheduleRequest;
import com.namo.spring.core.common.code.status.ErrorStatus;
import com.namo.spring.db.mysql.domains.schedule.dto.ScheduleParticipantQuery;
import com.namo.spring.db.mysql.domains.schedule.entity.Participant;
import com.namo.spring.db.mysql.domains.schedule.entity.Schedule;
import com.namo.spring.db.mysql.domains.schedule.exception.ScheduleException;
import com.namo.spring.db.mysql.domains.schedule.service.ParticipantService;
import com.namo.spring.db.mysql.domains.schedule.service.ScheduleService;
import com.namo.spring.db.mysql.domains.schedule.type.Location;
import com.namo.spring.db.mysql.domains.schedule.type.ParticipantRole;
import com.namo.spring.db.mysql.domains.schedule.type.Period;
import com.namo.spring.db.mysql.domains.schedule.type.ScheduleType;
import com.namo.spring.db.mysql.domains.user.entity.Member;
import com.namo.spring.db.mysql.domains.user.entity.User;
import com.namo.spring.db.mysql.domains.user.service.FriendshipService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ScheduleManageService {
    private final ScheduleMaker scheduleMaker;
    private final ScheduleService scheduleService;
    private final ParticipantManageService participantManageService;
    private final ParticipantService participantService;
    private final CategoryManageService categoryManageService;

    public Schedule getSchedule(Long scheduleId) {
        return scheduleService.readSchedule(scheduleId)
                .orElseThrow(() -> new ScheduleException(ErrorStatus.NOT_FOUND_SCHEDULE_FAILURE));
    }

    /**
     * 개인 일정을 찾아 반환합니다.
     * !! 개인 일정에는 유저의 생일 일정도 포함됩니다.
     * @param scheduleId
     * @return
     */
    public Schedule getPersonalSchedule(Long scheduleId) {
        Schedule schedule = getSchedule(scheduleId);
        if (schedule.getScheduleType() != ScheduleType.PERSONAL.getValue()) {
            throw new ScheduleException(ErrorStatus.NOT_PERSONAL_SCHEDULE);
        }
        return schedule;
    }

    public Schedule getMeetingSchedule(Long scheduleId) {
        Schedule schedule = scheduleService.readSchedule(scheduleId)
                .orElseThrow(() -> new ScheduleException(ErrorStatus.NOT_FOUND_SCHEDULE_FAILURE));
        if (schedule.getScheduleType() != ScheduleType.MEETING.getValue()) {
            throw new ScheduleException(ErrorStatus.NOT_MEETING_SCHEDULE);
        }
        return schedule;
    }

    public Schedule createPersonalSchedule(PersonalScheduleRequest.PostPersonalScheduleDto dto, Member member) {
        Period period = getValidatedPeriod(dto.getPeriod().getStartDate(), dto.getPeriod().getEndDate());
        Schedule schedule = scheduleMaker.createPersonalSchedule(dto, period, member.getNickname());
        participantManageService.createPersonalScheduleParticipant(member, schedule, dto.getCategoryId());
        return schedule;
    }

    public Schedule createMeetingSchedule(MeetingScheduleRequest.PostMeetingScheduleDto dto, Member owner) {
        validateParticipantCount(dto.getParticipants().size());
        List<Member> participants = participantManageService.getFriendshipValidatedParticipants(owner.getId(),
                dto.getParticipants());
        Period period = getValidatedPeriod(dto.getPeriod().getStartDate(), dto.getPeriod().getEndDate());
        Schedule schedule = scheduleMaker.createMeetingSchedule(dto, period);
        participantManageService.createMeetingScheduleParticipants(owner, schedule, participants);
        return schedule;
    }

    public List<ScheduleSummaryQuery> getMeetingScheduleSummaries(Long memberId) {
        return participantService.readScheduleParticipantSummaryByScheduleIds(memberId);
    }

    public List<Participant> getMyMonthlySchedules(Long memberId, LocalDate startDate, LocalDate endDate) {
        return participantService.readParticipantsWithScheduleAndCategoryByPeriod(memberId, null, startDate.atStartOfDay(), endDate.plusDays(1).atStartOfDay());
    }

    public List<Participant> getMemberMonthlySchedules(Member targetMember, LocalDate startDate, LocalDate endDate) {
        boolean birthdayVisible = targetMember.isBirthdayVisible();
        return participantService.readParticipantsWithScheduleAndCategoryByPeriod(targetMember.getId(), Boolean.TRUE,
                        startDate.atStartOfDay(), endDate.plusDays(1).atStartOfDay()).stream()
                .filter(participant -> participant.getCategory().isShared())
                // 생일 공유 여부에 따른 생일 일정 필터링
                .filter(participant ->
                {
                    if (participant.getSchedule().getScheduleType() == ScheduleType.BIRTHDAY.getValue()) {
                        return birthdayVisible;
                    }
                    return true;
                })
                .collect(Collectors.toList());
    }

    public List<ScheduleParticipantQuery> getMonthlyMembersSchedules(List<Long> memberIds, LocalDate startDate, LocalDate endDate,
            Long memberId) {
        validateParticipantCount(memberIds.size());
        List<Long> members = participantManageService.getFriendshipValidatedParticipants(memberId, memberIds)
                .stream()
                .map(Member::getId)
                .collect(Collectors.toList());
        members.add(memberId);
        return participantService.readParticipantsWithUserAndScheduleByPeriod(members, startDate.atStartOfDay(),
                        endDate.plusDays(1).atStartOfDay())
                .stream()
                // 다른 유저의 일정일 경우 공유 여부로 필터링
                .filter(participant -> isSharedOrOwnSchedule(participant, memberId, null))
                // 다른 유저의 생일 일정일 경우 생일 공유 여부로 필터링
                .filter(participant -> isBirthdayVisible(memberId, participant))
                .collect(Collectors.toList());
    }

    public List<ScheduleParticipantQuery> getMonthlyMeetingParticipantSchedules(Schedule schedule, LocalDate startDate, LocalDate endDate,
            Long memberId, Long anonymousId) {
        checkUserIsParticipant(schedule, memberId, null);
        List<Long> memberIds = participantService.readParticipantsByScheduleIdAndScheduleType(
                schedule.getId(), ScheduleType.MEETING).stream()
                .map(Participant::getUser)
                .map(User::getId)
                .collect(Collectors.toList());

        return participantService.readParticipantsWithUserAndScheduleByPeriod(memberIds, startDate.atStartOfDay(),
                        endDate.plusDays(1).atStartOfDay())
                .stream()
                // 다른 유저의 일정일 경우 공유 여부로 필터링
                .filter(participant -> isSharedOrOwnSchedule(participant, memberId, anonymousId))
                // 다른 유저의 생일 일정일 경우 생일 공유 여부로 필터링
                .filter(participant -> isBirthdayVisible(memberId, participant))
                .collect(Collectors.toList());
    }

    /**
     * 게스트 유저가 조회할 경우 공유된 일정만을,
     * 회원 유저가 조회할 경우 조회하는 회원의 비공개 일정까지 조회되도록
     * 필터링 합니다.
     * @param participant
     * @param memberId
     * @param anonymousId
     * @return 조회하는 회원의 비공개 일정 조회 여부
     */
    private boolean isSharedOrOwnSchedule(ScheduleParticipantQuery participant, Long memberId, Long anonymousId) {
        if (anonymousId != null) {
            return true;
        }
        // memberId가 있는 경우
        return participant.getMemberId().equals(memberId) || participant.getCategoryIsShared();
    }

    /**
     * 다른 유저의 생일일 경우
     * 그 유저의 생일 공유 여부로 생일 일정이 조회되도록
     * 필터링 합니다.
     * @param memberId
     * @param participant 일정과 참여자 정보
     * @return
     */
    private boolean isBirthdayVisible(Long memberId, ScheduleParticipantQuery participant) {
        if (!participant.getMemberId().equals(memberId) &&
                participant.getSchedule().getScheduleType() == ScheduleType.BIRTHDAY.getValue()) {
            return participant.getBirthdayVisible();
        }
        return true;
    }

    private void checkUserIsParticipant(Schedule schedule, Long memberId, Long anonymousId) {
        boolean isParticipant;
        if (memberId != null)
            isParticipant = participantService.existsByScheduleIdAndMemberId(schedule.getId(), memberId);
        else
            isParticipant = participantService.existsByScheduleIdAndAnonymousId(schedule.getId(), anonymousId);
        if (!isParticipant) {
            throw new ScheduleException(ErrorStatus.NOT_SCHEDULE_PARTICIPANT);
        }
    }

    public List<Participant> getMeetingScheduleParticipants(Schedule schedule, Long memberId, Long anonymousId) {
        checkUserIsParticipant(schedule, memberId, anonymousId);
        return participantService.readParticipantsByScheduleIdAndScheduleType(schedule.getId(), ScheduleType.MEETING);
    }

    public void updatePersonalSchedule(PersonalScheduleRequest.PatchPersonalScheduleDto dto, Schedule schedule,
            Long memberId) {
        Participant participant = validateAndGetScheduleOwner(schedule, memberId);
        Category category = categoryManageService.getMyCategory(memberId, dto.getCategoryId());
        participant.updateCategory(category);
        updateScheduleContent(dto.getTitle(), dto.getLocation(), dto.getPeriod(), null, schedule);
    }

    /**
     * 모임 일정의 정보를 수정합니다.
     */
    public void updateMeetingSchedule(MeetingScheduleRequest.PatchMeetingScheduleDto dto, Schedule schedule,
                                      Long memberId) {
        validateAndGetScheduleOwner(schedule, memberId);
        updateScheduleContent(dto.getTitle(), dto.getLocation(), dto.getPeriod(), dto.getImageUrl(), schedule);
        // 기존의 인원과, 초대될 & 삭제될 member  검증
        if (!dto.getParticipantsToAdd().isEmpty() || !dto.getParticipantsToRemove().isEmpty()) {
            List<Long> participantIds = getScheduleParticipantIds(schedule.getId());
            validateParticipantCount(
                    participantIds.size() + dto.getParticipantsToAdd().size() - dto.getParticipantsToRemove().size());
            validateExistingAndNewParticipantIds(participantIds, dto.getParticipantsToAdd());
            participantManageService.updateMeetingScheduleParticipants(memberId, schedule, dto);
        }
    }

    /**
     * 모임 일정의 제목, 이미지를 커스텀 합니다.
     */
    public void updateMeetingScheduleProfile(MeetingScheduleRequest.PatchMeetingScheduleProfileDto dto, Schedule schedule,
                                             Long memberId){
        Participant participant = participantManageService.getParticipantByMemberAndSchedule(memberId, schedule.getId());
        participant.updateCustomScheduleInfo(dto.getTitle(), dto.getImageUrl());
    }

    /**
     * 모임 일정에 대한 모든 참여자의 ID를 반환합니다,
     * 반환 값에는 활성, 비활성 모든 상태의 참여자가 포함됩니다.
     * 모임 일정 초대 인원 수를 검증하기 위해 사용합니다.
     *
     * @param scheduleId
     * @return 모임 일정에 대한 모든 참여자의 ID 배열
     */
    public List<Long> getScheduleParticipantIds(Long scheduleId) {
        List<Long> participantIds = participantService.readParticipantsByScheduleId(scheduleId).stream()
                .filter(participant -> participant.getIsOwner() == ParticipantRole.NON_OWNER.getValue())
                .map(Participant::getMember)
                .map(Member::getId)
                .collect(Collectors.toList());
        return participantIds;
    }

    private void updateScheduleContent(String title, MeetingScheduleRequest.LocationDto locationDto,
            MeetingScheduleRequest.PeriodDto periodDto, String imageUrl, Schedule schedule) {
        Period period = getValidatedPeriod(periodDto.getStartDate(), periodDto.getEndDate());
        Location location = locationDto != null ? LocationConverter.toLocation(locationDto) : null;
        schedule.updateContent(title, period, location, imageUrl);
    }

    public Participant validateAndGetScheduleOwner(Schedule schedule, Long memberId) {
        Participant participant = participantManageService.getParticipantWithScheduleAndMember(memberId,
                schedule.getId());
        if (participant.getIsOwner() != ParticipantRole.OWNER.getValue()) {
            throw new ScheduleException(ErrorStatus.NOT_SCHEDULE_OWNER);
        }
        return participant;
    }

    public void deleteSchedule(Schedule schedule, Long memberId){
        validateAndGetScheduleOwner(schedule, memberId);
        scheduleService.deleteSchedule(schedule.getId());
    }


    public void leaveMeetingSchedule(Schedule schedule, Long memberId) {
        Participant participant = participantManageService.getParticipantWithScheduleAndMember(schedule.getId(), memberId);
        // 방장이 모임을 나갈 경우
        if(participant.getIsOwner()==ParticipantRole.OWNER.getValue()){
            changeOwnerOrDeleteSchedule(schedule, participant);
        }
        // 방장이 아닌 유저가 모임을 나갈 경우
        else {
            participantManageService.deleteParticipant(participant, schedule);
        }
    }

    /**
     * !! 방장인 참여자가 모임을 나갈 경우
     * 남은 참여자의 유무에 따라
     * 방장을 재설정 또는 모임을 삭제합니다.
     *
     * 새로운 방장은 '가나다' 순으로 정렬하여
     * 가장 먼저 조회되는 참여자로 설정합니다.
     * @param schedule
     * @param participant 방장 participant
     */
    private void changeOwnerOrDeleteSchedule(Schedule schedule, Participant participant) {
        participantService.readFirstParticipantByScheduleId(schedule.getId()).ifPresentOrElse(
                // 가나다 순으로 가장 먼저 조회된 참여자에게 방장 권한 부여
                newOwner -> {
                    newOwner.setIsOwner(ParticipantRole.OWNER);
                    participant.setIsOwner(ParticipantRole.NON_OWNER);
                    participantManageService.deleteParticipant(participant, schedule);
                },
                // 참여자가 방장만 존재할 경우 모임 일정을 삭제
                () -> scheduleService.deleteSchedule(schedule.getId())
        );
    }

    public void createBirthdaySchedules(Member member) {
        scheduleMaker.createBirthdaySchedules(member).forEach(schedule ->
                participantManageService.createPersonalScheduleParticipant(member, schedule, null)
        );
    }

}
