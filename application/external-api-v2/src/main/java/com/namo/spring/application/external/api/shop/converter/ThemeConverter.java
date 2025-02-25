package com.namo.spring.application.external.api.shop.converter;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;

import com.namo.spring.application.external.api.shop.dto.ThemeResponse;
import com.namo.spring.db.mysql.domains.shop.entity.Theme;
import com.namo.spring.db.mysql.domains.shop.entity.ThemeDetailImage;

public class ThemeConverter {

    public static ThemeResponse.ThemeDtoList toThemeDtoList(Page<Theme> themes) {
        List<ThemeResponse.ThemePreviewDto> themeDtoList = themes.stream()
                .map(ThemeConverter::toThemeResponseDto)
                .collect(Collectors.toList());

        return ThemeResponse.ThemeDtoList.builder()
                .themes(themeDtoList)
                .totalPages(themes.getTotalPages())
                .currentPage(themes.getNumber() + 1)
                .pageSize(themes.getSize())
                .totalItems(themes.getTotalElements())
                .build();
    }

    public static ThemeResponse.ThemePreviewDto toThemeResponseDto(Theme theme) {
        return ThemeResponse.ThemePreviewDto.builder()
            .id(theme.getId())
            .name(theme.getName())
            .description(theme.getDescription())
            .price(theme.getPrice())
            .previewImageUrl(theme.getPreviewImageUrl())
            .build();
    }

    public static ThemeResponse.ThemeInfoDto toThemeInfoDto(Theme theme) {
        List<String> detailImages = theme.getDetailImages().stream()
            .map(ThemeDetailImage::getImageUrl)
            .toList();

        return ThemeResponse.ThemeInfoDto.builder()
            .id(theme.getId())
            .name(theme.getName())
            .description(theme.getDescription())
            .price(theme.getPrice())
            .detailImages(detailImages)
            .isOwned(theme.isOwned())
            .build();
    }
}
