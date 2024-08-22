package com.namo.spring.db.mysql.domains.schedule.service;

import com.namo.spring.core.common.annotation.DomainService;
import com.namo.spring.db.mysql.domains.schedule.dto.ScheduleParticipantQuery;
import com.namo.spring.db.mysql.domains.schedule.entity.Participant;
import com.namo.spring.db.mysql.domains.schedule.repository.ParticipantRepository;
import com.namo.spring.db.mysql.domains.schedule.type.ParticipantStatus;
import com.namo.spring.db.mysql.domains.schedule.type.ScheduleType;
import com.namo.spring.db.mysql.domains.user.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@DomainService
@RequiredArgsConstructor
public class ParticipantService {
    private final ParticipantRepository participantRepository;

    @Transactional
    public Participant createParticipant(Participant participant) {
        return participantRepository.save(participant);
    }

    @Transactional(readOnly = true)
    public Optional<Participant> readParticipant(Long id) {
        return participantRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<Participant> readScheduleParticipantItemsByScheduleIds(Member member) {
        return participantRepository.findParticipantsByMemberAndScheduleType(member, ScheduleType.MEETING.getValue());
    }

    @Transactional(readOnly = true)
    public Optional<Participant> readParticipantByScheduleIdAndMemberId(Long scheduleId, Long memberId) {
        return participantRepository.findParticipantByScheduleIdAndMemberId(scheduleId, memberId);
    }

    @Transactional(readOnly = true)
    public List<ScheduleParticipantQuery> readParticipantsWithScheduleAndMember(List<Long> memberIds, LocalDateTime startDate, LocalDateTime endDate) {
        return participantRepository.findParticipantsWithScheduleAndMember(memberIds, startDate, endDate);
    }

    @Transactional(readOnly = true)
    public List<Participant> readParticipantsByScheduleIdAndScheduleType(Long scheduleId, ScheduleType type, ParticipantStatus status) {
        return participantRepository.findParticipantsByScheduleIdAndScheduleType(scheduleId, type.getValue(), status);
    }

    public void deleteParticipant(Long id) {
        participantRepository.deleteById(id);
    }
}
