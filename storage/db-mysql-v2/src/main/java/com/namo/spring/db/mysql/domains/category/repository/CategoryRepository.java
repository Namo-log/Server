package com.namo.spring.db.mysql.domains.category.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.namo.spring.db.mysql.domains.category.entity.Category;
import com.namo.spring.db.mysql.domains.user.entity.Member;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    @Query("select c from Category c join c.member where c.id = :id and c.member= :member")
    Optional<Category> findCategoryByMemberAndId(@Param("id") Long id, @Param("member") Member member);

    @Query("select c from Category c join c.member where c.member= :member and c.type = '2'")
    Optional<Category> findMeetingCategoryByMember(@Param("member") Member member);

    @Query("select c from Category c join c.member where c.member= :member and c.type = '4'")
    Optional<Category> findBirthdayCategoryByMember(@Param("member") Member member);

    List<Category> findCategoryByMemberId(Long memberId);
}
