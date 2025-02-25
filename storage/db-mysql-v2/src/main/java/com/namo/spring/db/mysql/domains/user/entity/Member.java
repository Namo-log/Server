package com.namo.spring.db.mysql.domains.user.entity;

import static com.namo.spring.db.mysql.domains.user.utils.UserValidationUtils.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.type.SqlTypes;

import com.namo.spring.db.mysql.common.converter.MemberStatusConverter;
import com.namo.spring.db.mysql.common.model.BaseTimeEntity;
import com.namo.spring.db.mysql.domains.category.entity.Category;
import com.namo.spring.db.mysql.domains.category.entity.Palette;
import com.namo.spring.db.mysql.domains.notification.entity.Device;
import com.namo.spring.db.mysql.domains.point.entity.Point;
import com.namo.spring.db.mysql.domains.schedule.entity.Participant;
import com.namo.spring.db.mysql.domains.shop.entity.MemberTheme;
import com.namo.spring.db.mysql.domains.user.type.MemberRole;
import com.namo.spring.db.mysql.domains.user.type.MemberStatus;
import com.namo.spring.db.mysql.domains.user.type.SocialType;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DynamicInsert
public class Member extends BaseTimeEntity implements User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;

    @Column(unique = true)
    private String authId;

    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(length = 50)
    private String email;

    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(length = 50)
    private String name;

    @Column(nullable = false)
    private boolean nameVisible;

    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(length = 50)
    private String nickname;

    @Column(length = 4)
    private String tag;

    @Column(columnDefinition = "DATE")
    private LocalDate birthday;

    @Column(nullable = false)
    private boolean birthdayVisible;

    @JdbcTypeCode(SqlTypes.VARCHAR)
    private String bio;

    private String profileImage;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MemberRole memberRole;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SocialType socialType;

    @Column(nullable = false)
    private String socialRefreshToken;

    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Convert(converter = MemberStatusConverter.class)
    @Column(nullable = false, length = 50)
    private MemberStatus status;

    @ColumnDefault("NULL")
    private LocalDateTime deletedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "palette_id")
    private Palette palette;

    @Column(name = "sign_up_at")
    private LocalDateTime signUpAt;

    @Column(nullable = false)
    private boolean notificationEnabled;

    @OneToOne(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Point point;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Friendship> friendships = new HashSet<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Device> devices;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Category> categories;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Participant> participants;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    private List<MemberTheme> ownedThemes;

    @Builder
    public Member(String name, String tag, LocalDate birthday, String authId, String email,
            SocialType socialType, String socialRefreshToken) {
        this.name = name;
        this.nameVisible = true;
        this.tag = tag;
        this.email = email;
        this.authId = authId;
        this.birthday = birthday;
        this.birthdayVisible = true;
        this.memberRole = MemberRole.USER;
        this.status = MemberStatus.PENDING;
        this.socialType = socialType;
        this.socialRefreshToken = socialRefreshToken;
    }

    @Override
    public String toString() {
        return "Member ["
                + "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", birthday='" + birthday + '\'' +
                ", userRole='" + memberRole + '\'' +
                ", palette_id='" + palette.getId() + '\'' +
                ", status='" + status + '\'' +
                ", deletedAt='" + deletedAt + '\'' +
                ']';
    }

    public void changeToActive() {
        this.status = MemberStatus.ACTIVE;
    }

    public void changeToInactive() {
        this.status = MemberStatus.INACTIVE;
    }

    public void updateSocialRefreshToken(String socialRefreshToken) {
        this.socialRefreshToken = socialRefreshToken;
    }

    public boolean isSignUpComplete() {
        return !status.equals(MemberStatus.PENDING);
    }

    public void signUpComplete(String name, String nickname, LocalDate birthday, String bio,
            String tag, Palette palette, String profileImage) {
        this.name = name;
        this.nickname = validateNickname(nickname);
        this.birthday = validateBirthday(birthday);
        this.bio = validateBio(bio);
        this.tag = tag;
        this.palette = palette;
        this.status = MemberStatus.ACTIVE;
        this.profileImage = validateProfileImage(profileImage);
        this.signUpAt = LocalDateTime.now();
    }

    public void updatePalette(Palette palette){
        this.palette = palette;
    }

    public void updateNotificationEnabled(boolean notificationEnabled){
        this.notificationEnabled = notificationEnabled;
    }

    public void updateProfile(String nickname, boolean nameVisible, LocalDate birthday,
            boolean birthdayVisible, String bio, String profileImage) {
        this.nickname = validateNickname(nickname);
        this.nameVisible = nameVisible;
        this.birthday = validateBirthday(birthday);
        this.birthdayVisible = birthdayVisible;
        this.bio = validateBio(bio);
        this.profileImage = validateProfileImage(profileImage);
    }
}
