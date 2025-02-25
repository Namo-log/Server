package com.namo.spring.application.external.api.shop.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.namo.spring.application.external.api.shop.dto.ThemeResponse;
import com.namo.spring.application.external.api.shop.usecase.ShoppingUseCase;
import com.namo.spring.application.external.global.common.security.authentication.SecurityUserDetails;
import com.namo.spring.core.common.response.ResponseDto;
import com.namo.spring.db.mysql.domains.shop.enums.ThemeType;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

import lombok.RequiredArgsConstructor;

@Tag(name = "12. 테마샵", description = "테마샵 쇼핑 관련 API ")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v2/shop/themes")
public class ShoppingController {

    private final ShoppingUseCase shoppingUseCase;

    @GetMapping("")
    @Operation(summary = "판매 중인 테마 조회", description = "판매 중인 테마 목록을 조회합니다.")
    public ResponseDto<ThemeResponse.ThemeDtoList> getThemes(
            @AuthenticationPrincipal SecurityUserDetails memberInfo,
            @Parameter(description = "조회할 테마 타입입니다.", example = "background // profile")
            @RequestParam("type") String type,
            @Parameter(description = "1 부터 시작하는 페이지 번호입니다 (기본값 1)", example = "1")
            @RequestParam(value = "page", defaultValue = "1") int page,
            @Parameter(description = "한번에 조회할 테마 갯수 입니다 (기본값 6)", example = "6")
            @RequestParam(value = "size", defaultValue = "6") int size) {

        ThemeType themeType = ThemeType.valueOf(type.toUpperCase());

        return ResponseDto.onSuccess(shoppingUseCase
                .getThemesByType(themeType, page, size));
    }

    @GetMapping("{/themeId}")
    @Operation(summary = "테마 상세 조회", description = "테마 상세 정보를 조회합니다.")
    public ResponseDto<ThemeResponse.ThemeInfoDto> getThemeDetail(
            @AuthenticationPrincipal SecurityUserDetails memberInfo,
            @Parameter(description = "조회할 테마 ID입니다.", example = "1")
            @PathVariable("themeId") Long themeId) {

        return ResponseDto.onSuccess(shoppingUseCase
                .getThemeDetail(memberInfo.getUserId(), themeId));
    }
}
