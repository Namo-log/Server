package com.namo.spring.application.external.api.user.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.namo.spring.db.mysql.domains.user.model.query.MemberWithFriendshipStatusQuery;
import org.springframework.stereotype.Service;

import com.namo.spring.application.external.api.category.service.CategoryMaker;
import com.namo.spring.application.external.api.user.converter.MemberConverter;
import com.namo.spring.application.external.api.user.dto.MemberDto;
import com.namo.spring.db.mysql.domains.user.service.NicknameTag;
import com.namo.spring.core.common.code.status.ErrorStatus;
import com.namo.spring.db.mysql.domains.user.entity.Anonymous;
import com.namo.spring.db.mysql.domains.user.entity.Member;
import com.namo.spring.db.mysql.domains.user.entity.Term;
import com.namo.spring.db.mysql.domains.user.exception.MemberException;
import com.namo.spring.db.mysql.domains.user.repository.MemberRepository;
import com.namo.spring.db.mysql.domains.user.repository.TermRepository;
import com.namo.spring.db.mysql.domains.user.service.AnonymousService;
import com.namo.spring.db.mysql.domains.user.service.MemberService;
import com.namo.spring.db.mysql.domains.user.type.MemberStatus;
import com.namo.spring.db.mysql.domains.user.type.SocialType;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberManageService {

    private final MemberRepository memberRepository;
    private final TermRepository termRepository;
    private final MemberService memberService;
    private final CategoryMaker categoryMaker;
    private final AnonymousService anonymousService;

    public List<Term> getTerms(Member member) {
        return termRepository.findTermsByMember(member);
    }

    public Member getMember(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(ErrorStatus.NOT_FOUND_USER_FAILURE));
    }

    public Member getActiveMember(Long memberId){
        return memberService.readMemberByStatus(memberId, MemberStatus.ACTIVE)
                .orElseThrow(() -> new MemberException(ErrorStatus.NOT_FOUND_ACTIVE_USER_FAILURE));
    }

    public Member getActiveMemberByNicknameTag(String nicknameTag){
        NicknameTag findTarget = NicknameTag.from(nicknameTag);
        return memberService.readMemberByStatus(findTarget.getNickname(), findTarget.getTag(), MemberStatus.ACTIVE)
                .orElseThrow(() -> new MemberException(ErrorStatus.NOT_FOUND_ACTIVE_USER_FAILURE));
    }

    public Member getPendingMember(Long memberId){
        return memberService.readMemberByStatus(memberId, MemberStatus.PENDING)
                .orElseThrow(() -> new MemberException(ErrorStatus.NOT_FOUND_PENDING_USER_FAILURE));
    }

    public Optional<Member> getMemberByEmailAndSocialType(String email, SocialType socialType) {
        return memberRepository.findMemberByEmailAndSocialType(email, socialType);
    }

    public List<Member> getInactiveMembers() {
        return memberRepository.findMembersByStatusAndDate(MemberStatus.INACTIVE, LocalDateTime.now().minusDays(3));
    }

    public List<Member> getPendingMembers() {
        return memberRepository.findMembersByStatusAndDate(MemberStatus.PENDING, LocalDateTime.now().minusHours(1));
    }

    public Member saveMember(Member member) {
        return memberRepository.save(member);
    }

    public void removeMember(Member member) {
        memberRepository.delete(member);
    }

    public void checkEmailAndName(String email, String name) {
        if (email.isBlank() || name.isBlank()) {
            throw new MemberException(ErrorStatus.USER_POST_ERROR);
        }
    }

    public MemberDto.MemberCreationRecord updateExistingMember(Member existingMember, String socialRefreshToken) {
        existingMember.updateSocialRefreshToken(socialRefreshToken);
        return new MemberDto.MemberCreationRecord(existingMember, false);
    }

    public MemberDto.MemberCreationRecord createNewMember(Member member) {
        checkEmailAndName(member.getEmail(), member.getName());
        log.debug("Creating new social member");
        Member savedMember = memberService.createMember(member);
        makeBaseCategory(savedMember);
        return new MemberDto.MemberCreationRecord(savedMember, true);
    }

    public Member createNewAppleMember(String authId, String appleRefreshToken) {
        log.debug("Creating new apple member");
        Member newMember = memberService.createMember(MemberConverter.toMember(
                authId,
                appleRefreshToken,
                SocialType.APPLE));
        makeBaseCategory(newMember);
        return newMember;
    }

    public Member updateExistingAppleMember(Member existingMember, String appleRefreshToken) {
        existingMember.changeToActive();
        existingMember.updateSocialRefreshToken(appleRefreshToken);
        return existingMember;
    }

    private void makeBaseCategory(Member member) {
        categoryMaker.makePersonalCategory(member);
        categoryMaker.makeMeetingCategory(member);
        categoryMaker.makeBirthdayCategory(member);
    }

    public List<String> getMemberTagsByNickname(String nickname) {
        List<Member> members = memberService.readMemberByNickname(nickname);
        return members.stream()
                .map(Member::getTag)
                .toList();
    }

    public List<String> getUserTagsByNicname(String nickname) {
        List<String> tags = new ArrayList<>();
        tags.addAll(getMemberTagsByNickname(nickname));
        tags.addAll(anonymousService.readAnonymousNickname(nickname).stream()
                .map(Anonymous::getTag)
                .toList());
        return tags;
    }

    public void validateEmail(SocialType socialType, String email) {
        if (memberRepository.existsByEmailAndSocialType(email, socialType)) {
            throw new MemberException(ErrorStatus.DUPLICATE_EMAIL_FAILURE);
        }
    }

    public MemberWithFriendshipStatusQuery getMemberProfile(Long memberId, Long targetId) {
        return memberService.findMemberWithFriendshipStatus(memberId, targetId);
    }

}
