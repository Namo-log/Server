package com.namo.spring.application.external.api.record.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Getter;

public class ActivityRequest {

    @Getter
    public static class CreateActivityDto{
        @Schema(description = "활동 제목", example = "보드게임 밥디")
        @NotBlank
        private String title;

        @NotNull(message = "활동 참여할 참여자(participantIdList) 정보가 필요합니다.")
        @Schema(description = "활동에 참여할 participantId 를 넣어주세요", example = "[1, 2, 3]")
        private List<Long> participantIdList;

        @Schema(description = "활동 시작 시간", example = "2024-09-15 17:29:54")
        private LocalDateTime activityStartDate;

        @Schema(description = "활동 종료 시간", example = "2024-09-15 18:29:54")
        private LocalDateTime activityEndDate;

        @Schema(description = "활동 위치 정보")
        private ActivityLocationDto location;

        @Schema(description = "활동 정산 정보")
        private ActivitySettlementDto settlement;

        @Schema(description = "활동 태그", example = "술")
        private String tag;

        @NotNull(message = "활동에 대한 이미지가 없을 시 empty array를 전송하세요.")
        @Schema(description = "활동 이미지 URL List")
        private List<String> imageList;
    }

    @Getter
    public static class ActivityLocationDto{
        @Schema(description = "카카오맵 좌표계 상의 x 좌표")
        private Double longitude;

        @Schema(description = "카카오맵 좌표계 상의 y 좌표")
        private Double latitude;

        @Schema(description = "장소 이름", example = "스타벅스 강남역점")
        private String locationName;

        @Schema(description = "장소 카카오 맵 ID")
        private String kakaoLocationId;
    }

    @Getter
    public static class ActivitySettlementDto{
        @Schema(description = "활동 정산 총 금액", example = "150000.00")
        private BigDecimal totalAmount;

        @Schema(description = "정산 인원 수", example = "5")
        private int divisionCount;

        @Schema(description = "인당 금액", example = "300000.00")
        private BigDecimal amountPerPerson;

        @NotNull(message = "정산에 참여할 activityParticipantId 정보가 필요합니다.")
        @Schema(description = "정산에 참여 할 participantId 넣어주세요.", example = "[1, 2]" )
        private List<Long> participantIdList;
    }

    @Getter
    public static class UpdateActivityDto{
        @Schema(description = "활동 제목", example = "보드게임 밥디")
        private String title;

        @Schema(description = "활동 시작 시간", example = "2024-09-15 17:29:54")
        private LocalDateTime activityStartDate;

        @Schema(description = "활동 종료 시간", example = "2024-09-15 18:29:54")
        private LocalDateTime activityEndDate;

        @Schema(description = "활동 위치 정보")
        private ActivityLocationDto location;

        @NotNull(message = "활동에 대한 이미지가 없을 시 empty array를 전송하세요.")
        @Schema(description = "전체 활동 이미지 URL List")
        private List<String> imageList;

        @NotNull(message = "삭제할 배열이 없을 시 empty array를 전송하세요.")
        @Schema(description = "삭제할 이미지 ID 배열을 넣어주세요", example = "[1, 2, 3]")
        private List<Long> deleteImages;
    }

    @Getter
    public static class UpdateActivityParticipantsDto{
        @NotNull(message = "추가할 유저가 없을 시 empty array를 전송하세요.")
        @Schema(description = "스케줄에 추가할 유저 ID(userId), 추가할 유저가 없을 시 empty array를 전송합니다.")
        private List<Long> participantsToAdd;

        @NotNull(message = "삭제할 유저가 없을 시 empty array를 전송하세요.")
        @Schema(description = "스케줄에서 삭제할 참가자 ID(participantId), 삭제할 유저가 없을 시 empty array를 전송합니다.")
        private List<Long> participantsToRemove;
    }

    @Getter
    public static class UpdateActivitySettlementDto{
        @Schema(description = "활동 정산 총 금액", example = "150000.00")
        private BigDecimal totalAmount;

        @Schema(description = "정산 인원 수", example = "5")
        private int divisionCount;

        @Schema(description = "인당 금액", example = "300000.00")
        private BigDecimal amountPerPerson;

        @NotNull(message = "정산에 참여할 activityParticipantId 정보가 필요합니다.")
        @Schema(description = "정산에 참여 할 activityParticipantId 넣어주세요. (주의 : participantID가 아닙니다)", example = "[1, 2]" )
        private List<Long> activityParticipantId;
    }

}
