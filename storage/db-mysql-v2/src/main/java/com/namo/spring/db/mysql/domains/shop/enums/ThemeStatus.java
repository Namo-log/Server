package com.namo.spring.db.mysql.domains.shop.enums;

public enum ThemeStatus {
    SELLING,        // 판매 중
    SOLD_OUT,       // 품절
    DISCONTINUED,   // 판매 중지
    DELETED,        // 삭제 처리 (soft delete)
    PENDING,        // 판매 대기
    ARCHIVED        // 과거 아카이브 상태 (사용자는 볼 수 있지만 판매되지 않음)
}
