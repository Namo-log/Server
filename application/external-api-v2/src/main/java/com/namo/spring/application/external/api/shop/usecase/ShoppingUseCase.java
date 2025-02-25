package com.namo.spring.application.external.api.shop.usecase;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.namo.spring.application.external.api.shop.converter.ThemeConverter;
import com.namo.spring.application.external.api.shop.dto.ThemeResponse;
import com.namo.spring.application.external.api.shop.service.ThemeManageService;
import com.namo.spring.db.mysql.domains.shop.entity.Theme;
import com.namo.spring.db.mysql.domains.shop.enums.ThemeType;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ShoppingUseCase {

    private final ThemeManageService themeManageService;

    @Transactional(readOnly = true)
    public ThemeResponse.ThemeDtoList getThemesByType(ThemeType themeType, int page, int size) {
        Page<Theme> themes = themeManageService.getSellingThemesByType(themeType, page, size);
        return ThemeConverter.toThemeDtoList(themes);
    }

    @Transactional(readOnly = true)
    public ThemeResponse.ThemeInfoDto getThemeDetail(Long memberId, Long themeId) {
        Theme theme = themeManageService.getThemeByIdWithOwnership(memberId, themeId);
        return ThemeConverter.toThemeInfoDto(theme);
    }
}
