package com.pawn.jwtserver.config.auth.imagecode;

import com.pawn.jwtserver.config.auth.common.Code;

public class CaptchaCode extends Code {

    public CaptchaCode(String code,int expiredAfterSeconds){
      super(code,expiredAfterSeconds);
    }

}
