package com.namo.spring.db.mysql.domains.user.exception;

import com.namo.spring.core.common.code.BaseErrorCode;
import com.namo.spring.core.common.exception.GeneralException;

public class MemberException extends GeneralException {
    public MemberException(BaseErrorCode code) {
        super(code);
    }
}
