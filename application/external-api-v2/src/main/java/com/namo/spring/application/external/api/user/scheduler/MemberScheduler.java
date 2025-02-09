package com.namo.spring.application.external.api.user.scheduler;

import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.namo.spring.application.external.api.user.service.MemberManageService;
import com.namo.spring.application.external.api.user.service.SocialLoginService;
import com.namo.spring.db.mysql.domains.user.entity.Member;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class MemberScheduler {

    private final MemberManageService memberManageService;
    private final SocialLoginService socialLoginService;

    /**
     * 매일 자정에 실행되며, 탈퇴 이후 3일간 활동이 없는 사용자를 DB에서 삭제한다.
     */
    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void removeInactiveUsersFromDB() {
        List<Member> inactiveMembers = memberManageService.getInactiveMembers();
        for (Member member : inactiveMembers) {
            log.debug("[Delete] user name : " + member.getName());
            // TODO : 관련 삭제
            memberManageService.removeMember(member);
        }
    }

    @Scheduled(cron = "0 0 * * * *")
    @Transactional
    public void removePendingUsersFromDB() {
        List<Member> pendingMembers = memberManageService.getPendingMembers();
        for (Member member : pendingMembers) {
            log.debug("[Delete PENDING] user name: {}", member.getName());
            socialLoginService.unlinkSocialAccount(member);
            memberManageService.removeMember(member);
        }
    }

}
