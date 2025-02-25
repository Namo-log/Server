package com.namo.spring.db.mysql.domains.shop.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

import org.hibernate.annotations.DynamicInsert;

import com.namo.spring.db.mysql.common.model.BaseTimeEntity;
import com.namo.spring.db.mysql.domains.user.entity.Member;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DynamicInsert
public class MemberTheme extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Member member; // 사용자

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Theme theme; // 테마

    @Column(nullable = false)
    private Boolean isActive; // 활성화 여부

    @Column(nullable = false)
    private LocalDateTime purchasedAt; // 구매 시점

    @Builder
    public MemberTheme(Member member, Theme theme, Boolean isActive, LocalDateTime purchasedAt) {
        this.member = member;
        this.theme = theme;
        this.isActive = isActive;
        this.purchasedAt = LocalDateTime.now();
    }

}
