package com.namo.spring.application.external.api.user.dto;

import java.time.LocalDate;

import lombok.Builder;
import lombok.Getter;

public class ProfileResponse {

    @Getter
    @Builder
    public static class ProfileInfoDto{
        private String nickname;
        private String name;
        private String tag;
        private boolean isNameVisible;
        private LocalDate birthdate;
        private boolean isBirthdayVisible;
        private String bio;
        private String profileImage;
        private Long favoriteColorId;
    }
}
