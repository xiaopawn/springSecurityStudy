package com.pawn.courses.config.auth.imagecode;

import com.pawn.courses.config.auth.common.Code;

public class CaptchaCode extends Code {

    public CaptchaCode(String code,int expiredAfterSeconds){
      super(code,expiredAfterSeconds);
    }

}
