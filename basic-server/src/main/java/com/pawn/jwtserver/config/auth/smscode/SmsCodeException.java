package com.pawn.jwtserver.config.auth.smscode;

import org.springframework.security.core.AuthenticationException;

public class SmsCodeException  extends AuthenticationException {

    public SmsCodeException(String msg) {
        super(msg);
    }
}
