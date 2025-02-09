package com.namo.spring.application.external.global.config.security;

import org.springframework.stereotype.Component;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.namo.spring.application.external.api.user.service.MemberManageService;
import com.namo.spring.db.mysql.domains.user.entity.Member;

import lombok.RequiredArgsConstructor;

@Component("memberSecurity")
@RequiredArgsConstructor
public class MemberSecurity {

    private final MemberManageService memberManageService;

    /**
     * 주어진 userId에 해당하는 사용자가 회원가입을 완료했는지 검사합니다.
     * @param userId SecurityUserDetails에서 가져온 사용자 ID
     * @return 회원가입이 완료되어 있으면 true, 아니면 false
     */
    public boolean hasCompletedSignUp(Long userId) {
        Member member = memberManageService.getMember(userId);
        return member.isSignUpComplete();
    }
}
