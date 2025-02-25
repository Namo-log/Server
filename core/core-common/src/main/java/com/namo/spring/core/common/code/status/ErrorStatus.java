package com.namo.spring.core.common.code.status;

import static org.springframework.http.HttpStatus.*;

import org.springframework.http.HttpStatus;

import com.namo.spring.core.common.code.BaseErrorCode;
import com.namo.spring.core.common.response.ResponseDto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorStatus implements BaseErrorCode {

    /**
     * Common Error & Global Error
     */
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "잘못된 요청입니다."),
    METHOD_ARGUMENT_NOT_VALID(HttpStatus.BAD_REQUEST, "올바르지 않은 요청입니다."),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "인증과정에서 오류가 발생하였습니다."),
    FORBIDDEN(HttpStatus.FORBIDDEN, "금지된 요청입니다."),
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "지원하지 않은 Http Method 입니다."),

    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 에러가 발생했습니다."),

    /**
     * 400: Bad Request
     */
    MAKE_PUBLIC_KEY_FAILURE(HttpStatus.BAD_REQUEST, "애플 퍼블릭 키를 생성하는데 실패하였습니다"),
    APPLE_REQUEST_ERROR(HttpStatus.BAD_REQUEST, "애플 identityToken이 잘못되었습니다."),
    USER_POST_ERROR(HttpStatus.BAD_REQUEST, "email나 name이 비어있어 유저를 생성할 수 없습니다."),
    MEETING_INVALID_PARTICIPANT_NUMBER(HttpStatus.BAD_REQUEST, "모임의 인원은 2명 이상, 10명 이하 입니다."),
    MEETING_DUPLICATE_PARTICIPANT(HttpStatus.BAD_REQUEST, "중복되는 참여자입니다."),
    ALREADY_WRITTEN_DIARY_FAILURE(HttpStatus.BAD_REQUEST, "이미 일기를 작성하였습니다."),
    NOT_WRITTEN_DIARY_FAILURE(HttpStatus.BAD_REQUEST, "일기를 작성하지 않았습니다."),
    NOT_MY_DIARY_FAILURE(HttpStatus.BAD_REQUEST, "해당 일기에 대한 권한이 없습니다."),
    NOT_MEETING_SCHEDULE(HttpStatus.BAD_REQUEST, "모임 일정이 아닙니다."),
    NOT_PERSONAL_SCHEDULE(HttpStatus.BAD_REQUEST, "개인 일정이 아닙니다."),
    INVALID_FORMAT_FAILURE(HttpStatus.BAD_REQUEST, "유효한 날짜 값을 입력해주세요"),
    DUPLICATE_MEETING_PARTICIPANT(HttpStatus.BAD_REQUEST, "중복되는 참여자입니다."),
    INVALID_MEETING_PARTICIPANT_COUNT(HttpStatus.BAD_REQUEST, "모임 일정은 최소 1명, 최대 9명까지 초대 가능합니다."),
    CATEGORY_IN_USE_FAILURE(HttpStatus.BAD_REQUEST, "사용중인 카테고리는 삭제할 수 없습니다."),
    INVALID_NICKNAME_TAG_FORMAT(HttpStatus.BAD_REQUEST, "잘못된 닉네임#태그형식입니다."),
    INVALID_TAG_FORMAT(HttpStatus.BAD_REQUEST, "잘못된 태그 형식입니다."),
    INVALID_DEVICE_TYPE(HttpStatus.BAD_REQUEST, "잘못된 기기 유형입니다"),
    POINT_UNDER_ZERO(HttpStatus.BAD_REQUEST, "잔액이 부족합니다."),
    INVALID_AMOUNT(HttpStatus.BAD_REQUEST, "거래 금액은 0보다 커야 합니다."),
    NEGATIVE_BALANCE(HttpStatus.BAD_REQUEST, "잔액은 0보다 작을 수 없습니다."),

    /**
     * 401 : 소셜 로그인 오류
     */
    SOCIAL_LOGIN_FAILURE(HttpStatus.UNAUTHORIZED, "소셜 로그인에 실패하였습니다."),
    SOCIAL_WITHDRAWAL_FAILURE(HttpStatus.UNAUTHORIZED, "소셜 로그인 회원탈퇴에 실패하였습니다."),
    KAKAO_UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "카카오 accessToken이 잘못되었습니다"),
    NAVER_UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "네이버 accessToken이 잘못되었습니다"),
    APPLE_UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "애플 authorization code가 잘못되었습니다."),
    FORBIDDEN_ACCESS_TOKEN(HttpStatus.FORBIDDEN, "해당 토큰에는 엑세스 권한이 없습니다."),
    EXPIRED_TOKEN(HttpStatus.FORBIDDEN, "사용기간이 만료된 토큰입니다."),
    ANONYMOUS_LOGIN_FAILURE(HttpStatus.UNAUTHORIZED, "익명 사용자의 로그인에 실패했습니다."),

    /**
     * 403 : local Access Token 오류
     */
    EMPTY_ACCESS_KEY(HttpStatus.FORBIDDEN, "AccessToken 이 비어있습니다."),

    LOGOUT_ERROR(HttpStatus.FORBIDDEN, "로그 아웃된 사용자입니다."),

    EXPIRATION_TOKEN(HttpStatus.FORBIDDEN, "Token 이 만료되었습니다."),

    TAKEN_AWAY_TOKEN(HttpStatus.FORBIDDEN, "탈취당한 토큰입니다. 다시 로그인 해주세요."),

    WITHOUT_OWNER_REFRESH_TOKEN(HttpStatus.FORBIDDEN, "소유자가 아닌 RefreshToken 입니다."),

    EXPIRATION_ACCESS_TOKEN(HttpStatus.FORBIDDEN, "Access token 이 만료되었습니다."),

    EXPIRATION_REFRESH_TOKEN(HttpStatus.FORBIDDEN, "RefreshToken 이 만료되었습니다."),

    KAKAO_FORBIDDEN(HttpStatus.FORBIDDEN, "카카오 권한 오류"),

    NAVER_FORBIDDEN(HttpStatus.FORBIDDEN, "네이버 권한 오류"),

    MALFORMED_TOKEN(HttpStatus.UNAUTHORIZED, "비정상적인 토큰입니다."),

    TAMPERED_TOKEN(HttpStatus.UNAUTHORIZED, "서명이 조작된 토큰입니다."),

    UNSUPPORTED_JWT_TOKEN(HttpStatus.UNAUTHORIZED, "지원하지 않는 코튼입니다."),

    /**
     * 403 : 리소스 접근 권한 오류
     */
    AlREADY_FRIENDSHIP_MEMBER(HttpStatus.BAD_REQUEST, "이미 진행중인 친구 요청이거나 친구입니다."),

    NOT_MY_FRIENDSHIP_REQUEST(HttpStatus.BAD_REQUEST, "나에게 요청온 친구 요청이 아닙니다."),

    NOT_FRIENDSHIP_MEMBER(HttpStatus.BAD_REQUEST, "요청한 회원과 친구가 아닙니다."),

    NOT_SCHEDULE_OWNER(HttpStatus.FORBIDDEN, "해당 일정의 생성자가 아닙니다."),

    NOT_SCHEDULE_PARTICIPANT(HttpStatus.FORBIDDEN, "해당 일정의 참여자가 아닙니다."),

    NOT_ACTIVITY_PARTICIPANT(HttpStatus.FORBIDDEN, "해당 활동의 참여자가 아닌 유저가 포함되어 있습니다."),

    NOT_SCHEDULE_PARTICIPANT_OR_NOT_ACTIVE(HttpStatus.FORBIDDEN, "해당 일정의 참여자가 아닌 사람이 포함되어있거나 스케줄 초대를 수락하지 않은 사람이 포함되어있습니다."),

    IN_SETTLEMENT_ACTIVITY_MEMBER(HttpStatus.FORBIDDEN, "정산에 참여한 활동 참여자는 삭제할 수 없습니다."),
  
    /**
     * 404 : NOT FOUND 오류
     */
    NOT_FOUND_USER_FAILURE(NOT_FOUND, "유저를 찾을 수 없습니다."),

    NOT_FOUND_ACTIVE_USER_FAILURE(NOT_FOUND, "활성화된 유저를 찾을 수 없습니다. (가입되지 않은 유저거나, 회원가입이 완료되지 않았습니다.)"),

    NOT_FOUND_PENDING_USER_FAILURE(NOT_FOUND, "회원가입 대기중인 유저를 찾을 수 없습니다. (가입되지 않은 유저거나, 이미 가입이 완료되었습니다.)"),

    NOT_FOUND_SCHEDULE_FAILURE(NOT_FOUND, "일정을 찾을 수 없습니다. (참여한 일정이 아니거나, 일정 정보가 없습니다)"),

    NOT_FOUND_PARTICIPANT_FAILURE(NOT_FOUND, "일정의 참여자를 찾을 수 없습니다."),

    NOT_FOUND_CATEGORY_FAILURE(NOT_FOUND, "카테고리를 찾을 수 없습니다."),

    NOT_FOUND_PALETTE_FAILURE(NOT_FOUND, "팔레트를 찾을 수 없습니다."),

    NOT_FOUND_DIARY_FAILURE(NOT_FOUND, "다이어리를 찾을 수 없습니다."),

    NOT_FOUND_GROUP_DIARY_FAILURE(NOT_FOUND, "모임 메모 장소를 찾을 수 없습니다."),

    NOT_FOUND_GROUP_FAILURE(NOT_FOUND, "그룹을 찾을 수 없습니다."),

    NOT_FOUND_GROUP_AND_USER_FAILURE(NOT_FOUND, "모임 일정의 참여자가 아닙니다."),

    NOT_FOUND_GROUP_SCHEDULE_AND_USER_FAILURE(NOT_FOUND, "그룹 스케줄 구성원이 아닙니다."),

    NOT_FOUND_GROUP_MEMO_FAILURE(NOT_FOUND, "모임 메모를 찾을 수 없습니다."),

    NOT_FOUND_GROUP_MEMO_LOCATION_FAILURE(NOT_FOUND, "모임 활동을 찾을 수 없습니다."),

    NOT_FOUND_GROUP_ACTIVITY_FAILURE(NOT_FOUND, "모임 활동을 찾을 수 없습니다."),

    NOT_FOUND_ACTIVITY_IMG_FAILURE(NOT_FOUND, "모임 활동 이미지를 찾을 수 없습니다."),

    NOT_FOUND_COLOR(NOT_FOUND, "색깔을 찾을 수 없습니다."),

    NOT_FOUND_IMAGE(NOT_FOUND, "이미지를 찾을 수 없습니다."),

    NOT_FOUND_FRIENDSHIP_FAILURE(NOT_FOUND, "친구인 유저를 찾을 수 없습니다. (대기중인 요청일 수 있습니다)"),

    NOT_FOUND_MOBILE_DEVICE_FAILURE(NOT_FOUND, "유저의 모바일 기기 정보를 찾을 수 없습니다."),

    NOT_FOUND_DEVICE_FAILURE(NOT_FOUND, "유저의 기기 정보를 찾을 수 없습니다."),

    NOT_PARTICIPATING_ACTIVITY(NOT_FOUND, "참여한 활동이 아닙니다." ),

    NOT_FOUND_FRIENDSHIP_REQUEST(NOT_FOUND, "친구 요청을 찾을 수 없습니다."),
    NOT_FOUND_POINT_REQUEST(NOT_FOUND, "대기중인 포인트 입금 요청을 찾을 수 없습니다."),
    NOT_FOUND_THEME(NOT_FOUND, "테마를 찾을 수 없습니다."),

    /**
     * 404 : 예외 상황 에러
     */
    NOT_DELETE_BASE_CATEGORY_FAILURE(NOT_FOUND, "일정 및 모임 카테고리는 삭제 될 수 없습니다."),

    NOT_CHANGE_SPECIFIED_NAME_FAILURE(NOT_FOUND, "일정 및 모임은 기본 카테고리로 지정된 이름입니다."),

    NOT_INCLUDE_OWNER_IN_REQUEST(NOT_FOUND, "모임 생성자는 참가자 목록에 포함될 수 없습니다."),

    NOT_CHECK_TERM_ERROR(NOT_FOUND, "약관에 무조건 동의 해야합니다."),

    GROUP_MEMO_IS_FULL_ERROR(NOT_FOUND, "모임 메모가 가득 차 있습니다."),

    NOT_INCLUDE_GROUP_USER(NOT_FOUND, "모임 안에 포함되어 있지 않은 유저입니다."),

    EMPTY_USERS_FAILURE(NOT_FOUND, "모임 일정에 참여글 유저가 없습니다."),

    NOT_HAS_GROUP_CATEGORIES_USERS(NOT_FOUND, "유저들에 대한 모임의 카테고리가 없습니다."),

    INVALID_DATE(NOT_FOUND, "시작 날짜가 종료 날짜 이전 이어야 합니다."),

    INVALID_ALARM(NOT_FOUND, "알림 시간이 유효하지 않습니다."),

    SCHEDULE_PARTICIPANT_IS_EMPTY_ERROR(NOT_FOUND, "모임 일정의 참여자가 없습니다."),

    NOT_SUPPORTED_DEVICE_TYPE(NOT_FOUND, "푸쉬 알림 전송이 지원되지 않는 기기입니다."),

    /**
     * 404 : 중복 에러
     */
    DIARY_EXISTS_FAILURE(NOT_FOUND, "이미 존재하는 다이어리 입니다."),

    DUPLICATE_PARTICIPATE_FAILURE(NOT_FOUND, "이미 가입한 그룹입니다."),

    DUPLICATE_GROUP_MEMO_FAILURE(NOT_FOUND, "이미 모임 메모가 생성되어 있습니다."),

    DUPLICATE_EMAIL_FAILURE(NOT_FOUND, "이미 해당 소셜 이메일이 가입되어 있습니다."),

    /**
     * 404 : 오용 오류
     */
    NOT_USERS_CATEGORY(NOT_FOUND, "해당 유저의 카테고리가 아닙니다."),

    NOT_USERS_IN_GROUP(NOT_FOUND, "유저가 그룹에 포함되어 있지 않습니다."),

    NOT_IMAGE_IN_DIARY(NOT_FOUND, "이미지가 다이어리에 포함되어 있지 않습니다."),

    NOT_FILTERTYPE_OF_ARCHIVE(NOT_FOUND, "올바른 필터 타입이 아닙니다"),

    /**
     * 404 : 인프라 에러
     */
    FILE_NAME_EXCEPTION(NOT_FOUND, "파일 확장자가 잘못되었습니다."),

    S3_UPLOAD_FAILURE(NOT_FOUND, "파일 업로드 과정에서 오류가 발생하였습니다."),

    S3_DELETE_FAILURE(NOT_FOUND, "파일 삭제 과정에서 오류가 발생하였습니다."),

    NAVER_NOT_FOUND(NOT_FOUND, "[네이버] 검색 결과가 없습니다"),

    /**
     * 404 : IllegalArgumentException
     */
    NOT_NULL_FAILURE(NOT_FOUND, "널 혹은 비어 있는 값을 카테고리 값으로 넣지 말아주세요,"),

    /**
     * 404 : 서버 에러
     */
    INTERNET_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 오류"),

    JPA_FAILURE(HttpStatus.INTERNAL_SERVER_ERROR, "jpa, sql 상에서 오류가 발생했습니다."),

    KAKAO_INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "카카오 서버 오류"),

    KAKAO_BAD_GATEWAY(HttpStatus.INTERNAL_SERVER_ERROR, "카카오 시스템 오류"),

    KAKAO_SERVICE_UNAVAILABLE(HttpStatus.INTERNAL_SERVER_ERROR, "카카오 서비스 점검 중"),

    FEIGN_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "feign 서버 에러");

    private final HttpStatus code;
    private final String message;

    public int getCode() {
        return code.value();
    }

    public HttpStatus getHttpStatus() {
        return code;
    }

    @Override
    public ResponseDto.ErrorReasonDto getReason() {
        return ResponseDto.ErrorReasonDto.builder()
                .isSuccess(false)
                .code(getCode())
                .message(message)
                .build();
    }

    @Override
    public ResponseDto.ErrorReasonDto getReasonHttpStatus() {
        return ResponseDto.ErrorReasonDto.builder()
                .status(this.code)
                .isSuccess(false)
                .code(getCode())
                .message(message)
                .build();
    }
}
