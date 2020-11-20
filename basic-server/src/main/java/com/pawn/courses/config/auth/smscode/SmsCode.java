package com.pawn.courses.config.auth.smscode;

import com.pawn.courses.config.auth.common.Code;

public class SmsCode extends Code {

    private String mobile;

    public SmsCode(String code,int expiredAfterSeconds,String mobile){
        super(code,expiredAfterSeconds);
        this.mobile = mobile;
    }

}
