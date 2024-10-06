package com.namo.spring.application.external.api.user.facade;

import java.util.List;
import java.util.Map;

import com.namo.spring.application.external.api.schedule.service.ScheduleMaker;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.namo.spring.application.external.api.user.converter.MemberConverter;
import com.namo.spring.application.external.api.user.converter.MemberResponseConverter;
import com.namo.spring.application.external.api.user.converter.TermConverter;
import com.namo.spring.application.external.api.user.dto.MemberDto;
import com.namo.spring.application.external.api.user.dto.MemberRequest;
import com.namo.spring.application.external.api.user.dto.MemberResponse;
import com.namo.spring.application.external.api.user.helper.JwtAuthHelper;
import com.namo.spring.application.external.api.user.service.MemberManageService;
import com.namo.spring.application.external.api.user.service.SocialLoginService;
import com.namo.spring.application.external.api.user.service.TagGenerator;
import com.namo.spring.application.external.global.common.security.jwt.CustomJwts;
import com.namo.spring.db.mysql.domains.category.entity.Palette;
import com.namo.spring.db.mysql.domains.category.service.PaletteService;
import com.namo.spring.db.mysql.domains.user.entity.Member;
import com.namo.spring.db.mysql.domains.user.type.SocialType;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthFacade {
    private final JwtAuthHelper jwtAuthHelper;
    private final SocialLoginService socialLoginService;
    private final MemberManageService memberManageService;
    private final TagGenerator tagGenerator;
    private final PaletteService paletteService;
    private final ScheduleMaker scheduleMaker;

    @Transactional
    public MemberResponse.SignUpDto socialSignup(MemberRequest.SocialSignUpDto signUpDto, SocialType socialType) {
        Map<String, String> socialUserInfo = socialLoginService.getSocialUserInfo(signUpDto, socialType);
        return processSocialSignup(signUpDto, socialType, socialUserInfo);
    }

    @Transactional
    public MemberResponse.SignUpDto signupApple(MemberRequest.AppleSignUpDto req) {
        String appleRefreshToken = socialLoginService.getAppleRefreshToken(req);
        String authId = socialLoginService.getAppleAuthId(req);
        return processAppleSignup(req, authId, appleRefreshToken);
    }

    private MemberResponse.SignUpDto processSocialSignup(MemberRequest.SocialSignUpDto signUpDto, SocialType socialType,
            Map<String, String> socialUserInfo) {
        Member member = MemberConverter.toMember(socialUserInfo, signUpDto.getSocialRefreshToken(), socialType);
        MemberDto.MemberCreationRecord memberInfo = processOrRetrieveMember(member, socialType,
                signUpDto.getSocialRefreshToken());
        return createSignUpResponse(memberInfo.member(), memberInfo.isNewUser());
    }

    private MemberResponse.SignUpDto processAppleSignup(MemberRequest.AppleSignUpDto req, String authId,
            String appleRefreshToken) {
        MemberDto.MemberCreationRecord memberInfo = processOrRetrieveAppleMember(req, appleRefreshToken, authId);
        return createSignUpResponse(memberInfo.member(), memberInfo.isNewUser());
    }

    private MemberDto.MemberCreationRecord processOrRetrieveAppleMember(MemberRequest.AppleSignUpDto req,
            String appleRefreshToken, String authId) {
        return memberManageService.getMemberByEmailAndSocialType(authId, SocialType.APPLE)
                .map(existingMember -> new MemberDto.MemberCreationRecord(
                        memberManageService.updateExistingAppleMember(existingMember, appleRefreshToken),
                        false))
                .orElseGet(() -> new MemberDto.MemberCreationRecord(
                        memberManageService.createNewAppleMember(authId, appleRefreshToken),
                        true));
    }

    private MemberDto.MemberCreationRecord processOrRetrieveMember(Member member, SocialType socialType,
            String socialRefreshToken) {
        return memberManageService.getMemberByEmailAndSocialType(member.getEmail(), socialType)
                .map(existingMember -> memberManageService.updateExistingMember(existingMember, socialRefreshToken))
                .orElseGet(() -> memberManageService.createNewMember(member));
    }

    private MemberResponse.SignUpDto createSignUpResponse(Member savedMember, boolean isNewUser) {
        List<MemberResponse.TermsDto> terms = TermConverter.toTerms(memberManageService.getTerms(savedMember));
        CustomJwts jwts = jwtAuthHelper.createToken(savedMember);
        return MemberResponseConverter.toSignUpDto(jwts.accessToken(), jwts.refreshToken(), isNewUser,
                savedMember.isSignUpComplete(), terms);
    }

    @Transactional
    public MemberResponse.ReissueDto reissueAccessToken(String refreshToken) {
        Pair<Long, CustomJwts> member = jwtAuthHelper.refresh(refreshToken);
        return MemberResponseConverter.toReissueDto(member.getValue().accessToken(), member.getValue().refreshToken());
    }

    @Transactional
    public void logout(Long memberId, String accessToken, String refreshToken) {
        jwtAuthHelper.removeJwtsToken(memberId, accessToken, refreshToken);
    }

    @Transactional
    public void removeSocialMember(Long memberId, String accessToken, String refreshToken) {
        Member member = memberManageService.getMember(memberId);
        socialLoginService.unlinkSocialAccount(member);
        jwtAuthHelper.removeJwtsToken(memberId, accessToken, refreshToken);
        member.changeToInactive();
    }

    @Transactional
    public Member completeSignup(MemberRequest.CompleteSignUpDto dto, Long memberId) {
        Member member = memberManageService.getPendingMember(memberId);
        String tag = tagGenerator.generateTag(member.getNickname());
        Palette palette = paletteService.getPalette(dto.getColorId());
        member.signUpComplete(dto.getName(), dto.getNickname(), dto.getBirthday(), dto.getBio(), tag, palette,
                dto.getProfileImage());
        memberManageService.saveMember(member);
        scheduleMaker.createBirthdaySchedules(member);
        return member;
    }
}
