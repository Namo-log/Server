package com.namo.spring.application.external.api.schedule.converter;

import com.namo.spring.application.external.api.schedule.dto.ScheduleRequest;
import com.namo.spring.application.external.api.schedule.dto.ScheduleResponse;
import com.namo.spring.db.mysql.domains.category.entity.Category;
import com.namo.spring.db.mysql.domains.schedule.entity.Participant;
import com.namo.spring.db.mysql.domains.schedule.entity.Schedule;
import com.namo.spring.db.mysql.domains.schedule.type.Period;

public class ScheduleConverter {
	private ScheduleConverter() {
		throw new IllegalStateException("Util Class");
	}

	public static Schedule toSchedule(String title, Period period, ScheduleRequest.LocationDto location, int type,
		String imageUrl, Integer participantCount, String participantNames) {
		return Schedule.builder()
			.title(title)
			.period(period)
			.location(location != null ? LocationConverter.toLocation(location) : null)
			.scheduleType(type)
			.imageUrl(imageUrl)
			.participantCount(participantCount)
			.participantNicknames(participantNames)
			.build();
	}

	public static ScheduleResponse.ScheduleSummaryDto toScheduleSummaryDto(Participant participant) {
		Schedule schedule = participant.getSchedule();
		return ScheduleResponse.ScheduleSummaryDto.builder()
			.scheduleId(schedule.getId())
			.scheduleTitle(schedule.getTitle())
			.scheduleStartDate(schedule.getPeriod().getStartDate())
			.locationInfo(LocationConverter.toLocationInfoDto(schedule.getLocation()))
			.categoryInfo(toCategoryInfoDto(participant.getCategory()))
			.build();
	}

	private static ScheduleResponse.CategoryInfoDto toCategoryInfoDto(Category category) {
		return ScheduleResponse.CategoryInfoDto.builder()
			.name(category.getName())
			.colorId(category.getPalette().getId())
			.build();
	}

}
