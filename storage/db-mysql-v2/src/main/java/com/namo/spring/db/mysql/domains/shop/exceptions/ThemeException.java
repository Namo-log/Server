package com.namo.spring.db.mysql.domains.shop.exceptions;

import com.namo.spring.core.common.code.BaseErrorCode;
import com.namo.spring.core.common.exception.GeneralException;

public class ThemeException extends GeneralException {
    public ThemeException(BaseErrorCode code) {
        super(code);
    }
}
