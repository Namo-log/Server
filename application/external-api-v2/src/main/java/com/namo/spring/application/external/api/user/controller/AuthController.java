package com.namo.spring.application.external.api.user.controller;

import jakarta.validation.Valid;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.namo.spring.application.external.api.user.converter.MemberConverter;
import com.namo.spring.application.external.api.user.dto.MemberRequest;
import com.namo.spring.application.external.api.user.dto.MemberResponse;
import com.namo.spring.application.external.api.user.facade.AuthFacade;
import com.namo.spring.application.external.global.common.security.authentication.SecurityUserDetails;
import com.namo.spring.core.common.response.ResponseDto;
import com.namo.spring.db.mysql.domains.user.entity.Member;
import com.namo.spring.db.mysql.domains.user.type.SocialType;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import lombok.RequiredArgsConstructor;

@Tag(name = "00. Auth", description = "로그인, 회원가입 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v2/auths")
public class AuthController{

    private final AuthFacade memberFacade;

    @Operation(summary = "애플 회원가입", description = "애플 소셜 로그인을 통한 회원가입을 진행합니다.")
    @PostMapping(value = "/signup/apple")
    @PreAuthorize("isAnonymous()")
    public ResponseDto<MemberResponse.SignUpDto> appleSignup(
            @Valid @RequestBody MemberRequest.AppleSignUpDto dto
    ) {
        MemberResponse.SignUpDto signupDto = memberFacade.signupApple(dto);
        return ResponseDto.onSuccess(signupDto);
    }

    @Operation(summary = "소셜 회원가입", description = "카카오,네이버 소셜 로그인을 통한 회원가입을 진행합니다.")
    @PostMapping(value = "/signup/{socialType}")
    @PreAuthorize("isAnonymous()")
    public ResponseDto<MemberResponse.SignUpDto> socialSignup(
            @Valid @RequestBody MemberRequest.SocialSignUpDto dto,
            @PathVariable(value = "socialType") SocialType socialType
    ) {
        MemberResponse.SignUpDto signupDto = memberFacade.socialSignup(dto, socialType);
        return ResponseDto.onSuccess(signupDto);
    }

    @Operation(summary = "회원가입 완료", description = "회원가입 완료 처리를 진행합니다. 이 과정을 거치지 않으면 소셜 연결만 되어있는 상태입니다.")
    @PostMapping(value = "/signup/complete")
    public ResponseDto<MemberResponse.SignUpDoneDto> completeSignup(
            @Valid @RequestBody MemberRequest.CompleteSignUpDto dto,
            @AuthenticationPrincipal SecurityUserDetails member
    ) {
        Member target = memberFacade.completeSignup(dto, member.getUserId());
        return ResponseDto.onSuccess(MemberConverter.toSignUpDoneDto(target));
    }

    @PreAuthorize("@memberSecurity.hasCompletedSignUp(#member.userId)")
    @Operation(summary = "토큰 재발급", description = "토큰 재발급")
    @PostMapping(value = "/reissuance")
    public ResponseDto<MemberResponse.ReissueDto> reissueAccessToken(
            @AuthenticationPrincipal SecurityUserDetails member,
            @RequestHeader(value = "refreshToken") String refreshToken
    ) {
        return ResponseDto.onSuccess(memberFacade.reissueAccessToken(refreshToken));
    }

    @Operation(summary = "로그아웃", description = "로그아웃 API, 로그아웃 처리된 유저의 토큰을 만료시킵니다.")
    @PostMapping(value = "/logout")
    @PreAuthorize("isAuthenticated()")
    public ResponseDto<String> logout(
            @RequestHeader(value = "Authorization") String authHeader,
            @RequestHeader(value = "refreshToken") String refreshToken,
            @AuthenticationPrincipal SecurityUserDetails member
    ) {
        String accessToken = authHeader.split(" ")[1];
        memberFacade.logout(member.getUserId(), accessToken, refreshToken);
        return ResponseDto.onSuccess("로그아웃 되었습니다.");
    }

    @Operation(summary = "소셜 회원 탈퇴", description = """
            회원 탈퇴 API, 소셜 회원 탈퇴 처리를 진행합니다.
            회원 탈퇴 시, 소셜 회원 정보를 삭제하고, 회원의 애플리케이션 연결을 해제합니다.
            		
            이때, 삭제 처리는 바로 진행되는 것이 아니며 탈퇴 신청 후 3일간 유예기간이 있습니다.
            """)
    @PostMapping("/delete/{socialType}")
    @PreAuthorize("isAuthenticated()")
    public ResponseDto<String> removeAuthUser(
            @RequestHeader(value = "Authorization") String authHeader,
            @RequestHeader(value = "refreshToken") String refreshToken,
            @AuthenticationPrincipal SecurityUserDetails member
    ) {
        String accessToken = authHeader.split(" ")[1];
        memberFacade.removeSocialMember(member.getUserId(), accessToken, refreshToken);
        return ResponseDto.onSuccess("회원탈퇴가 완료되었습니다.");
    }

    @Operation(summary = "!!!!!!!!!!!!!!!!즉시 회원 탈퇴!!!!!!!!!!!!", description = """
            즉시 회원을 탈퇴합니다. 절대 복구가 불가능합니다
            테스트용으로만 사용해주세요!!!!!!!!!!!!!!!!
            """)
    @DeleteMapping("/admin/{socialType}")
    @PreAuthorize("isAuthenticated()")
    public ResponseDto<String> removeUserImmediate(
            @RequestHeader(value = "Authorization") String authHeader,
            @RequestHeader(value = "refreshToken") String refreshToken,
            @AuthenticationPrincipal SecurityUserDetails member
    ) {
        String accessToken = authHeader.split(" ")[1];
        memberFacade.removeMemberNow(member.getUserId(), accessToken, refreshToken);
        return ResponseDto.onSuccess("즉시 회원탈퇴가 완료되었습니다.");
    }

}
