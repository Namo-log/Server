package com.namo.spring.db.mysql.domains.individual.repository.category;

import java.util.List;

import org.springframework.data.repository.query.Param;

import com.namo.spring.db.mysql.domains.individual.domain.Category;
import com.namo.spring.db.mysql.domains.individual.type.CategoryKind;
import com.namo.spring.db.mysql.domains.user.domain.User;

public interface CategoryRepositoryCustom {
	List<Category> findMoimCategoriesByUsers(@Param("users") List<User> users, @Param("kind") CategoryKind kind);
}
