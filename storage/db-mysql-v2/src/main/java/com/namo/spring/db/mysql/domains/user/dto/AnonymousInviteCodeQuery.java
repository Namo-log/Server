package com.namo.spring.db.mysql.domains.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AnonymousInviteCodeQuery {
    private String code;
}
