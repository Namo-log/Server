package com.namo.spring.application.external.api.shop.dto;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class ThemeResponse {

    @Builder
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ThemeDtoList {
        @Schema(description = "총 페이지 수", example = "5")
        private int totalPages;
        @Schema(description = "현재 페이지 번호", example = "1")
        private int currentPage;
        @Schema(description = "한 페이지당 항목 수", example = "20")
        private int pageSize;
        @Schema(description = "전체 항목 수", example = "100")
        private long totalItems;
        private List<ThemePreviewDto> themes;

    }

    @Builder
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ThemePreviewDto {
        private Long id;
        private String name;
        private String description;
        private Integer price;
        private String previewImageUrl;
    }

    @Builder
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ThemeInfoDto {
        private Long id;
        private String name;
        private String description;
        private Integer price;
        private String type;
        private List<String> detailImages;
        private Boolean isOwned; // 테마 보유 여부
    }
}
