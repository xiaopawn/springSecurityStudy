package com.pawn.jwtserver.config.auth.common;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Code {

    private String code;

    private LocalDateTime expireTime;

    public Code(String code, int expiredAfterSeconds) {
        this.code = code;
        this.expireTime = LocalDateTime.now().plusSeconds(expiredAfterSeconds);
    }

    public boolean isExpired(){
        return LocalDateTime.now().isAfter(expireTime);
    }
}
