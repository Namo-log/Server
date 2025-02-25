package com.namo.spring.db.mysql.domains.shop.service;

import com.namo.spring.core.common.annotation.DomainService;
import com.namo.spring.db.mysql.domains.shop.repository.MemberThemeRepository;

import lombok.RequiredArgsConstructor;

@DomainService
@RequiredArgsConstructor
public class MemberThemeService {

    private final MemberThemeRepository memberThemeRepository;

    public boolean purchaseInfo(Long memberId, Long themeId) {
        return memberThemeRepository.existsByMemberIdAndThemeId(memberId, themeId);
    }
}
