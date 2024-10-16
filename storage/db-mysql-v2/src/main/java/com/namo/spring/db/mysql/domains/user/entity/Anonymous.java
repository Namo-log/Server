package com.namo.spring.db.mysql.domains.user.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.util.StringUtils;

import com.namo.spring.db.mysql.common.model.BaseTimeEntity;
import com.namo.spring.db.mysql.domains.user.type.Password;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DynamicInsert
public class Anonymous extends BaseTimeEntity implements User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;

    @JdbcTypeCode(SqlTypes.VARCHAR)
    private String name;

    private Boolean nameVisible;

    @Column(nullable = false, length = 4)
    private String tag;

    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(nullable = false, length = 50)
    private String nickname;

    @Column(nullable = false, length = 50)
    @Embedded
    private Password password;

    private String inviteCode;

    @Builder
    public Anonymous(String name, Boolean nameVisible, String tag,
            String nickname, String password, String inviteCode) {
        if (!StringUtils.hasText(nickname))
            throw new IllegalArgumentException("nickname은 null이거나 빈 문자열일 수 없습니다.");
        if (!StringUtils.hasText(tag))
            throw new IllegalArgumentException("tag는 null이거나 빈 문자열일 수 없습니다.");
        this.name = name;
        this.nameVisible = nameVisible;
        this.tag = tag;
        this.nickname = nickname;
        this.password = Password.encrypt(password);
        this.inviteCode = inviteCode;
    }

    public static Anonymous of(String name, Boolean nameVisible, String tag,
            String nickname, String password, String inviteCode) {
        return Anonymous.builder()
                .nickname(nickname)
                .name(name)
                .nameVisible(nameVisible)
                .tag(tag)
                .password(password)
                .inviteCode(inviteCode)
                .build();
    }
}
