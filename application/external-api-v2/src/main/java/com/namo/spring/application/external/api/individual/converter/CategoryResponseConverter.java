package com.namo.spring.application.external.api.individual.converter;

import java.util.List;
import java.util.stream.Collectors;

import com.namo.spring.db.mysql.domains.individual.domain.Category;
import com.namo.spring.application.external.api.individual.dto.CategoryResponse;

public class CategoryResponseConverter {

	private CategoryResponseConverter() {
		throw new IllegalStateException("Utility class");
	}

	public static CategoryResponse.CategoryIdDto toCategoryIdDto(Category category) {
		return new CategoryResponse.CategoryIdDto(category.getId());
	}

	public static List<CategoryResponse.CategoryDto> toCategoryDtoList(List<Category> categories) {
		return categories.stream()
			.map(CategoryResponseConverter::toCategoryDto)
			.collect(Collectors.toList());
	}

	public static CategoryResponse.CategoryDto toCategoryDto(Category category) {
		return new CategoryResponse.CategoryDto(
			category.getId(),
			category.getName(),
			category.getPalette().getId(),
			category.getShare()
		);
	}
}
