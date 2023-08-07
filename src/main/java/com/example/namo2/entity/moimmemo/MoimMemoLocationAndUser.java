package com.example.namo2.entity.moimmemo;

import com.example.namo2.entity.BaseTimeEntity;
import com.example.namo2.entity.user.User;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "moim_memo_location_and_user")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MoimMemoLocationAndUser extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "moim_memo_location_user_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "moim_memo_location_id")
    private MoimMemoLocation moimMemoLocation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Builder
    public MoimMemoLocationAndUser(Long id, MoimMemoLocation moimMemoLocation, User user) {
        this.id = id;
        this.moimMemoLocation = moimMemoLocation;
        this.user = user;
    }
}
