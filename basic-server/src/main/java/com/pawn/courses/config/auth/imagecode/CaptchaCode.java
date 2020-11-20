package com.pawn.courses.config.auth.imagecode;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CaptchaCode {

    private String code;

    private LocalDateTime expireTime;

    public CaptchaCode(String code,int expiredAfterSeconds){
        this.code = code;
        this.expireTime = LocalDateTime.now().plusSeconds(expiredAfterSeconds);
    }

    public boolean isExpired(){
        return LocalDateTime.now().isAfter(expireTime);
    }
}
