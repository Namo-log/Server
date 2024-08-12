package com.namo.spring.db.mysql.domains.user.entity;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.type.SqlTypes;
import org.springframework.util.StringUtils;

import com.namo.spring.db.mysql.common.converter.MemberRoleConverter;
import com.namo.spring.db.mysql.common.converter.MemberStatusConverter;
import com.namo.spring.db.mysql.common.model.BaseTimeEntity;
import com.namo.spring.db.mysql.domains.user.type.MemberRole;
import com.namo.spring.db.mysql.domains.user.type.MemberStatus;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DynamicInsert
@SQLDelete(sql = "UPDATE member SET deleted_at = NOW(), status = 'INACVTIVE' WHERE id = ?")
public class Member extends BaseTimeEntity implements User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(nullable = false)
	private Long id;

	@JdbcTypeCode(SqlTypes.VARCHAR)
	@Column(nullable = false, length = 50)
	private String email;

	@JdbcTypeCode(SqlTypes.VARCHAR)
	@Column(nullable = false, length = 50)
	private String name;

	@Column(nullable = false)
	private boolean nameVisible;

	@JdbcTypeCode(SqlTypes.VARCHAR)
	@Column(nullable = false, length = 50)
	private String nickname;

	@Column(nullable = false, length = 4)
	private String tag;

	@JdbcTypeCode(SqlTypes.VARCHAR)
	@Column(length = 10)
	private String birthday;  // "MM-DD"

	@Column(nullable = false)
	private boolean birthdayVisible;

	@JdbcTypeCode(SqlTypes.VARCHAR)
	private String bio;

	@OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
	private Set<Friendship> friendships = new HashSet<>();

	@JdbcTypeCode(SqlTypes.VARCHAR)
	@Convert(converter = MemberRoleConverter.class)
	@Column(nullable = false, length = 50)
	private MemberRole memberRole;

	@JdbcTypeCode(SqlTypes.VARCHAR)
	@Convert(converter = MemberStatusConverter.class)
	@Column(nullable = false, length = 50)
	private MemberStatus status;

	@ColumnDefault("NULL")
	private LocalDateTime deletedAt;

	@Builder
	public Member(String name, String tag, String email, String birthday, MemberRole userRole, MemberStatus status) {
		if (!StringUtils.hasText(name))
			throw new IllegalArgumentException("name은 null이거나 빈 문자열일 수 없습니다.");
		else if (!StringUtils.hasText(email))
			throw new IllegalArgumentException("email은 null이거나 빈 문자열일 수 없습니다.");
		this.name = name;
		this.nameVisible = true;
		this.tag = tag;
		this.email = email;
		this.birthday = birthday;
		this.birthdayVisible = true;
		this.memberRole = Objects.requireNonNull(userRole, "memberRole은 null일 수 없습니다.");
		this.status = Objects.requireNonNull(status, "status는 null일 수 없습니다.");
	}

	@Override
	public String toString() {
		return "Member ["
			+ "id='" + id + '\'' +
			", name='" + name + '\'' +
			", email='" + email + '\'' +
			", birthday='" + birthday + '\'' +
			", userRole='" + memberRole + '\'' +
			", status='" + status + '\'' +
			", deletedAt='" + deletedAt + '\'' +
			']';
	}
}
