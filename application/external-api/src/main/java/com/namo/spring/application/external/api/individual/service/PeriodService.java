package com.namo.spring.application.external.api.individual.service;

import com.namo.spring.core.common.code.status.ErrorStatus;
import com.namo.spring.core.common.exception.PersonalException;
import com.namo.spring.db.mysql.domains.individual.type.Period;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PeriodService {

    public void checkValidDate(Period period) {
        if (period.getStartDate().isAfter(period.getEndDate())) {
            throw new PersonalException(ErrorStatus.INVALID_DATE);
        }
    }
}
