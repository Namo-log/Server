package com.namo.spring.application.external.api.user.converter;

import com.namo.spring.application.external.api.user.dto.ProfileResponse;
import com.namo.spring.db.mysql.domains.user.entity.Member;

public class ProfileConverter {

    public static ProfileResponse.ProfileInfoDto toProfileInfoDto(Member member){
        return ProfileResponse.ProfileInfoDto.builder()
                .nickname(member.getNickname())
                .name(member.getName())
                .tag(member.getTag())
                .isNameVisible(member.isNameVisible())
                .isBirthdayVisible(member.isBirthdayVisible())
                .birthdate(member.getBirthday())
                .bio(member.getBio())
                .profileImage(member.getProfileImage())
                .favoriteColorId(member.getPalette().getId())
                .build();
    }
}
