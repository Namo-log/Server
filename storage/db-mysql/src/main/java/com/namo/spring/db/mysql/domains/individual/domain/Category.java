package com.namo.spring.db.mysql.domains.individual.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

import com.namo.spring.db.mysql.common.model.BaseTimeEntity;
import com.namo.spring.db.mysql.domains.individual.type.CategoryKind;
import com.namo.spring.db.mysql.domains.individual.type.CategoryStatus;
import com.namo.spring.db.mysql.domains.user.domain.User;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Category extends BaseTimeEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "category_id")
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "palette_id")
	private Palette palette;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private User user;

	@Column(length = 20)
	private String name;

	@Column(nullable = false, columnDefinition = "TINYINT(1)")
	private Boolean share;

	@Enumerated(EnumType.STRING)
	private CategoryStatus status;

	@Enumerated(EnumType.STRING)
	private CategoryKind kind;

	@Builder
	public Category(Palette palette, User user, String name, Boolean share, CategoryKind kind) {
		this.palette = palette;
		this.user = user;
		this.name = name;
		this.share = share;
		this.status = CategoryStatus.ACTIVE;
		this.kind = kind;
	}

	public void update(String name, Boolean share, Palette palette) {
		this.name = name;
		this.share = share;
		this.palette = palette;
	}

	public void delete() {
		this.status = CategoryStatus.DELETE;
	}

	public boolean isNotCreatedByUser(Long userId) {
		return this.user.getId() != userId;
	}

	public boolean isBaseCategory() {
		if (kind == CategoryKind.SCHEDULE || kind == CategoryKind.MOIM) {
			return true;
		}
		return false;
	}
}
