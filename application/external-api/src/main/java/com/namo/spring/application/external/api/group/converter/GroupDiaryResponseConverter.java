package com.namo.spring.application.external.api.group.converter;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.SliceImpl;

import com.namo.spring.application.external.api.group.dto.GroupDiaryResponse;
import com.namo.spring.application.external.api.group.dto.MeetingDiaryResponse;
import com.namo.spring.core.common.utils.DateUtil;
import com.namo.spring.db.mysql.domains.group.domain.MoimMemo;
import com.namo.spring.db.mysql.domains.group.domain.MoimMemoLocation;
import com.namo.spring.db.mysql.domains.group.domain.MoimMemoLocationAndUser;
import com.namo.spring.db.mysql.domains.group.domain.MoimMemoLocationImg;
import com.namo.spring.db.mysql.domains.group.domain.MoimScheduleAndUser;

public class GroupDiaryResponseConverter {
	private GroupDiaryResponseConverter() {
		throw new IllegalStateException("Utill Classes");
	}

	public static MeetingDiaryResponse.MeetingDiaryDto toMeetingDiaryDto(
		MoimMemo groupMemo,
		List<MoimMemoLocation> groupActivities,
		List<MoimMemoLocationAndUser> groupActivityAndUsers) {
		List<MeetingDiaryResponse.MeetingUserDto> users = groupMemo.getMoimSchedule().getMoimScheduleAndUsers().stream()
			.map(GroupDiaryResponseConverter::toMeetingUserDto)
			.toList();
		return MeetingDiaryResponse.MeetingDiaryDto.fromMeetingMemo(groupMemo,
			toMeetingActivityDtos(groupActivities, groupActivityAndUsers));
	}

	public static GroupDiaryResponse.GroupDiaryDto toGroupDiaryDto(
		MoimMemo groupMemo,
		List<MoimMemoLocation> groupActivities,
		List<MoimMemoLocationAndUser> groupActivityAndUsers) {
		List<GroupDiaryResponse.GroupUserDto> users = groupMemo.getMoimSchedule().getMoimScheduleAndUsers().stream()
			.map(GroupDiaryResponseConverter::toGroupUserDto)
			.toList();
		return GroupDiaryResponse.GroupDiaryDto.fromMoimMemo(groupMemo,
			toGroupActivityDtos(groupActivities, groupActivityAndUsers));
	}

	public static MeetingDiaryResponse.MeetingUserDto toMeetingUserDto(MoimScheduleAndUser groupScheduleAndUser) {
		return MeetingDiaryResponse.MeetingUserDto
			.builder()
			.userId(groupScheduleAndUser.getUser().getId())
			.userName(groupScheduleAndUser.getUser().getName())
			.build();
	}

	/**
	 * v1
	 */
	public static GroupDiaryResponse.GroupUserDto toGroupUserDto(MoimScheduleAndUser groupScheduleAndUser) {
		return GroupDiaryResponse.GroupUserDto
			.builder()
			.userId(groupScheduleAndUser.getUser().getId())
			.userName(groupScheduleAndUser.getUser().getName())
			.build();
	}

	private static List<MeetingDiaryResponse.MeetingActivityDto> toMeetingActivityDtos(
		List<MoimMemoLocation> groupActivities,
		List<MoimMemoLocationAndUser> groupActivityAndUsers) {
		Map<MoimMemoLocation, List<MoimMemoLocationAndUser>> groupActivityMappingUsers = groupActivityAndUsers
			.stream()
			.collect(Collectors.groupingBy(MoimMemoLocationAndUser::getMoimMemoLocation));

		return groupActivities.stream()
			.map(groupActivity -> toMeetingActivityDto(groupActivityMappingUsers, groupActivity))
			.collect(Collectors.toList());
	}

	/**
	 * v1
	 */
	private static List<GroupDiaryResponse.MoimActivityDto> toGroupActivityDtos(
		List<MoimMemoLocation> groupActivities,
		List<MoimMemoLocationAndUser> groupActivityAndUsers) {
		Map<MoimMemoLocation, List<MoimMemoLocationAndUser>> groupActivityMappingUsers = groupActivityAndUsers
			.stream()
			.collect(Collectors.groupingBy(MoimMemoLocationAndUser::getMoimMemoLocation));
		return groupActivities.stream()
			.map(groupActivity -> toGroupActivityDto(groupActivityMappingUsers, groupActivity))
			.collect(Collectors.toList());
	}

	private static MeetingDiaryResponse.MeetingActivityDto toMeetingActivityDto(
		Map<MoimMemoLocation, List<MoimMemoLocationAndUser>> groupActivityMappingUsers,
		MoimMemoLocation groupActivity) {
		List<String> urls = groupActivity.getMoimMemoLocationImgs().stream()
			.map(MoimMemoLocationImg::getUrl)
			.toList();
		List<Long> participants = groupActivityMappingUsers.get(groupActivity).stream()
			.map(groupActivityAndUser -> groupActivityAndUser.getUser().getId())
			.toList();
		return MeetingDiaryResponse.MeetingActivityDto
			.builder()
			.meetingActivityId(groupActivity.getId())
			.name(groupActivity.getName())
			.money(groupActivity.getTotalAmount())
			.images(groupActivity.getMoimMemoLocationImgs().stream()
				.map(GroupDiaryResponseConverter::toMeetingActivityImageDto)
				.collect(Collectors.toList()))
			.participants(participants)
			.build();
	}

	/**
	 * v1
	 */
	private static GroupDiaryResponse.MoimActivityDto toGroupActivityDto(
		Map<MoimMemoLocation, List<MoimMemoLocationAndUser>> groupActivityMappingUsers,
		MoimMemoLocation groupActivity) {
		List<Long> participants = groupActivityMappingUsers.get(groupActivity).stream()
			.map(groupActivityAndUser -> groupActivityAndUser.getUser().getId())
			.toList();
		return GroupDiaryResponse.MoimActivityDto
			.builder()
			.moimActivityId(groupActivity.getId())
			.name(groupActivity.getName())
			.money(groupActivity.getTotalAmount())
			.images(groupActivity.getMoimMemoLocationImgs().stream()
				.map(GroupDiaryResponseConverter::toGroupActivityImageDto)
				.collect(Collectors.toList()))
			.participants(participants)
			.build();
	}

	/**
	 * v1
	 */
	private static GroupDiaryResponse.MoimActivityImageDto toGroupActivityImageDto(MoimMemoLocationImg image) {
		return GroupDiaryResponse.MoimActivityImageDto.builder()
			.id(image.getId())
			.url(image.getUrl())
			.build();
	}

	private static MeetingDiaryResponse.MeetingActivityImageDto toMeetingActivityImageDto(MoimMemoLocationImg image) {
		return MeetingDiaryResponse.MeetingActivityImageDto.builder()
			.id(image.getId())
			.url(image.getUrl())
			.build();
	}

	public static MeetingDiaryResponse.SliceDiaryDto toSliceDiaryDto(
		List<MoimScheduleAndUser> groupScheduleAndUsers,
		Pageable page
	) {
		boolean hasNext = false;
		if (groupScheduleAndUsers.size() > page.getPageSize()) {
			groupScheduleAndUsers.remove(page.getPageSize());
			hasNext = true;
		}
		SliceImpl<MoimScheduleAndUser> groupSchedulesSlice = new SliceImpl<>(groupScheduleAndUsers, page, hasNext);
		return MeetingDiaryResponse.SliceDiaryDto.builder()
			.content(
				groupSchedulesSlice.stream().map(GroupDiaryResponseConverter::toDiaryDto).collect(Collectors.toList()))
			.currentPage(groupSchedulesSlice.getNumber())
			.size(groupSchedulesSlice.getSize())
			.first(groupSchedulesSlice.isFirst())
			.last(groupSchedulesSlice.isLast())
			.build();
	}

	public static MeetingDiaryResponse.DiaryDto toDiaryDto(MoimScheduleAndUser groupScheduleAndUser) {
		return MeetingDiaryResponse.DiaryDto.builder()
			.scheduleId(groupScheduleAndUser.getMoimSchedule().getId())
			.name(groupScheduleAndUser.getMoimSchedule().getName())
			.startDate(DateUtil.toSeconds(groupScheduleAndUser.getMoimSchedule().getPeriod().getStartDate()))
			.contents(groupScheduleAndUser.getMemo())
			.images(groupScheduleAndUser.getMoimSchedule().getMoimMemo()
				.getMoimMemoLocations()
				.stream()
				.flatMap(location -> location
					.getMoimMemoLocationImgs()
					.stream())
				.map(GroupDiaryResponseConverter::toMeetingDiaryImageDto)
				.collect(Collectors.toList()))
			.categoryId(groupScheduleAndUser.getCategory().getId())
			.color(groupScheduleAndUser.getCategory().getPalette().getId())
			.placeName(groupScheduleAndUser.getMoimSchedule().getLocation().getLocationName())
			.build();
	}

	public static MeetingDiaryResponse.MeetingDiaryImageDto toMeetingDiaryImageDto(MoimMemoLocationImg image) {
		return MeetingDiaryResponse.MeetingDiaryImageDto.builder()
			.id(image.getId())
			.url(image.getUrl())
			.build();
	}

}
