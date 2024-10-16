package com.namo.spring.db.mysql.domains.user.service;

import java.util.List;
import java.util.Optional;

import org.springframework.transaction.annotation.Transactional;

import com.namo.spring.core.common.annotation.DomainService;
import com.namo.spring.db.mysql.domains.user.entity.Member;
import com.namo.spring.db.mysql.domains.user.entity.Term;
import com.namo.spring.db.mysql.domains.user.repository.TermRepository;
import com.namo.spring.db.mysql.domains.user.type.Content;

import lombok.RequiredArgsConstructor;

@DomainService
@RequiredArgsConstructor
public class TermService {
    private final TermRepository termRepository;

    @Transactional
    public Term createTerm(Term term) {
        return termRepository.save(term);
    }

    @Transactional(readOnly = true)
    public List<Term> readTermListByMemberId(Long memberId) {
        return termRepository.findByMemberId(memberId);
    }

    @Transactional(readOnly = true)
    public Optional<Term> readTerm(Content content, Member member) {
        return termRepository.findTermByContentAndMember(content, member);
    }

    @Transactional
    public void deleteTerm(Long termId) {
        termRepository.deleteById(termId);
    }

}
