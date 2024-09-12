package com.namo.spring.db.mysql.domains.record.entity;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.util.StringUtils;

import com.namo.spring.db.mysql.common.model.BaseTimeEntity;
import com.namo.spring.db.mysql.domains.schedule.entity.Schedule;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DynamicInsert
public class Activity extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(nullable = false)
    private Schedule schedule;

    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(nullable = false, length = 50)
    private String title;

    @Column(nullable = false)
    private BigDecimal totalAmount;

    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(length = 50)
    private String categoryTag;

    @OneToMany(mappedBy = "activity", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ActivityParticipant> participants;

    @OneToMany(mappedBy = "activity", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ActivityImage> activityImages;

    @Builder
    public Activity(Schedule schedule, String title, BigDecimal totalAmount, String categoryTag) {
        if (!StringUtils.hasText(title))
            throw new IllegalArgumentException("title은 null이거나 빈 문자열일 수 없습니다.");
        this.schedule = Objects.requireNonNull(schedule, "schedule은 null일 수 없습니다.");
        this.title = title;
        this.totalAmount = totalAmount;
        this.categoryTag = categoryTag;
    }
}
