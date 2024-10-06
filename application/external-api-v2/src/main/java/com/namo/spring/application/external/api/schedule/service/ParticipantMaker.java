package com.namo.spring.application.external.api.schedule.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.namo.spring.db.mysql.domains.category.entity.Category;
import com.namo.spring.db.mysql.domains.category.entity.Palette;
import com.namo.spring.db.mysql.domains.category.service.CategoryService;
import com.namo.spring.db.mysql.domains.category.service.PaletteService;
import com.namo.spring.db.mysql.domains.schedule.entity.Participant;
import com.namo.spring.db.mysql.domains.schedule.entity.Schedule;
import com.namo.spring.db.mysql.domains.schedule.service.ParticipantService;
import com.namo.spring.db.mysql.domains.schedule.type.ParticipantRole;
import com.namo.spring.db.mysql.domains.user.entity.Anonymous;
import com.namo.spring.db.mysql.domains.user.entity.Member;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class ParticipantMaker {
    private final PaletteService paletteService;
    private final CategoryService categoryService;
    private final ParticipantService participantService;

    public void makeScheduleOwner(Schedule schedule, Member member, Long categoryId, Long paletteId) {
        Category category;
        if (categoryId != null) {
            category = categoryService.readCategoryByMemberAndId(categoryId, member);
        } else {
            if(schedule.getIsMeetingSchedule()) {
                category = categoryService.readMeetingCategoryByMember(member);
            }
            else category = categoryService.readBirthdayCategoryByMember(member);
        }
        Palette palette = paletteId != null ? paletteService.getPalette(paletteId) :  null;

        Participant participant;
        if(schedule.getIsMeetingSchedule()){
            participant = Participant.of(ParticipantRole.OWNER.getValue(), member, schedule, category, palette, schedule.getTitle(), schedule.getImageUrl());
        } else participant = Participant.of(ParticipantRole.OWNER.getValue(), member, schedule, category, palette, null, null);
        participantService.createParticipant(participant);
    }

    public void makeMeetingScheduleParticipants(Schedule schedule, List<Member> members) {
        List<Participant> participants = members.stream()
                .map(member -> {
                    Category category = categoryService.readMeetingCategoryByMember(member);
                    return Participant.of(ParticipantRole.NON_OWNER.getValue(), member, schedule,
                            category, member.getPalette(), schedule.getTitle(), schedule.getImageUrl());
                })
                .collect(Collectors.toList());
        participantService.createParticipants(participants);
    }

    public Participant makeGuestParticipant(Schedule schedule, Anonymous anonymous, Long paletteId) {
        Palette palette = paletteService.getPalette(paletteId);
        Participant participant = Participant.of(ParticipantRole.NON_OWNER.getValue(), anonymous, schedule,
                null, palette, schedule.getTitle(), schedule.getImageUrl());
        Participant savedParticipant = participantService.createParticipant(participant);
        schedule.setGuestParticipantsInfo(anonymous.getNickname());
        return savedParticipant;
    }

}
