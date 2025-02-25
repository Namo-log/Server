package com.namo.spring.db.mysql.domains.shop.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

import org.hibernate.annotations.DynamicInsert;

import com.namo.spring.db.mysql.common.model.BaseTimeEntity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DynamicInsert
public class ThemeDetailImage extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String imageUrl; // 상세 이미지 URL

    private Integer orderIndex; // 이미지 순서

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Theme theme; // 연결된 테마


    @Builder
    public ThemeDetailImage(String imageUrl, Integer orderIndex, Theme theme) {
        this.imageUrl = imageUrl;
        this.orderIndex = orderIndex;
        this.theme = theme;
    }
}
