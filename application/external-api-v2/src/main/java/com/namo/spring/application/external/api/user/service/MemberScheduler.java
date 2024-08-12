package com.namo.spring.application.external.api.user.service;

import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.namo.spring.db.mysql.domains.user.entity.Member;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class MemberScheduler {

	private final MemberLoginService memberService;

	/**
	 * 매일 자정에 실행되며, 탈퇴 이후 3일간 활동이 없는 사용자를 DB에서 삭제한다.
	 */
	@Scheduled(cron = "0 0 0 * * *")
	@Transactional
	public void removeInactiveUsersFromDB() {
		List<Member> inactiveMembers = memberService.getInactiveMember();
		for (Member member : inactiveMembers) {
			log.debug("[Delete] user name : " + member.getName());
			// TODO : 관련 삭제
			memberService.removeMember(member);
		}
	}

}
