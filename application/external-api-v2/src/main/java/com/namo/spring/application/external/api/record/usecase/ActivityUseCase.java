package com.namo.spring.application.external.api.record.usecase;

import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.namo.spring.application.external.api.record.converter.ActivityResponseConverter;
import com.namo.spring.application.external.api.record.dto.ActivityRequest;
import com.namo.spring.application.external.api.record.dto.ActivityResponse;
import com.namo.spring.application.external.api.record.serivce.ActivityManageService;
import com.namo.spring.application.external.api.record.serivce.ActivityParticipantManageService;
import com.namo.spring.application.external.api.schedule.service.ParticipantManageService;
import com.namo.spring.db.mysql.domains.record.entity.Activity;
import com.namo.spring.db.mysql.domains.record.entity.ActivityParticipant;
import com.namo.spring.db.mysql.domains.schedule.entity.Schedule;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ActivityUseCase {

    private final ParticipantManageService participantManageService;
    private final ActivityManageService activityManageService;
    private final ActivityParticipantManageService activityParticipantManageService;

    @Transactional(readOnly = true)
    public List<ActivityResponse.ActivityInfoDto> getActivities(Long memberId, Long scheduleId) {
        Schedule schedule = participantManageService.getMyParticipant(memberId, scheduleId).getSchedule();
        List<Activity> activities = schedule.getActivityList();

        return activities.stream()
                .map(ActivityResponseConverter::toActivityInfoDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public ActivityResponse.ActivitySettlementInfoDto getSettlement(Long memberId, Long activityId) {
        Activity activity = activityManageService.getMyActivity(memberId, activityId);
        return ActivityResponseConverter.toActivitySettlementInfoDto(activity);
    }

    @Transactional
    public void createActivity(Long memberId, Long scheduleId, ActivityRequest.CreateActivityDto request) {
        Activity activity = activityManageService.createActivity(memberId, scheduleId, request);
        List<ActivityParticipant> activityParticipant = activityParticipantManageService.createActivityParticipant(
                activity, request.getParticipantIdList());
        if (request.getSettlement()!=null){
            activity.setSettlementInfo(request.getSettlement().getTotalAmount());
            activityParticipantManageService.addSettlement(request.getSettlement(), activityParticipant);
        }
    }
}
