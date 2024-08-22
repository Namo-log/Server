package com.namo.spring.application.external.api.record.Controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.namo.spring.application.external.api.record.dto.DiaryRequest;
import com.namo.spring.application.external.api.record.usecase.DiaryUseCase;
import com.namo.spring.application.external.global.common.security.authentication.SecurityUserDetails;
import com.namo.spring.core.common.response.ResponseDto;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v2/diaries")
public class DiaryController {

	private final DiaryUseCase diaryUseCase;

	@PostMapping("")
	public ResponseDto<String> createDiary(
		@AuthenticationPrincipal SecurityUserDetails memberInfo,
		@RequestBody DiaryRequest.CreateDiaryDto request
	) {
		diaryUseCase.createDiary(memberInfo, request);
		return ResponseDto.onSuccess("기록 생성 성공");
	}

}
