package com.namo.spring.db.mysql.domains.shop.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.namo.spring.db.mysql.domains.shop.entity.MemberTheme;

public interface MemberThemeRepository extends JpaRepository<MemberTheme, Long> {
    boolean existsByMemberIdAndThemeId(Long memberId, Long themeId);
}
