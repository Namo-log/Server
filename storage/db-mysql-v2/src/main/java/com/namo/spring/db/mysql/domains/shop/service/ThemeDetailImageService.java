package com.namo.spring.db.mysql.domains.shop.service;

import com.namo.spring.core.common.annotation.DomainService;
import com.namo.spring.db.mysql.domains.shop.repository.ThemeDetailImageRepository;

import lombok.RequiredArgsConstructor;

@DomainService
@RequiredArgsConstructor
public class ThemeDetailImageService {

    private final ThemeDetailImageRepository themeDetailImageRepository;

}
