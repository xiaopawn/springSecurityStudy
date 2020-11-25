package com.pawn.jwtserver.controller;

import com.pawn.commons.exception.CustomException;
import com.pawn.commons.exception.CustomExceptionType;
import com.pawn.jwtserver.config.auth.MyUserDetails;
import com.pawn.jwtserver.config.auth.MyUserDetailsServiceMapper;
import com.pawn.jwtserver.config.auth.exception.AjaxResponse;
import com.pawn.jwtserver.config.auth.smscode.SmsCode;
import com.pawn.jwtserver.utils.MyContants;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.Objects;

@Slf4j
@RestController
public class SmsCodeController {

    @Resource
    private MyUserDetailsServiceMapper myUserDetailsServiceMapper;

    @GetMapping("getSmsCode")
    public AjaxResponse getSmsCode(@RequestParam String mobile, HttpSession session){

        MyUserDetails userDetails = myUserDetailsServiceMapper.findByUserName(mobile);

        if (Objects.isNull(userDetails)){
            return AjaxResponse.error(new CustomException(CustomExceptionType.SYSTEM_ERROR,"用户信息不存在"));
        }
        SmsCode smsCode = new SmsCode(RandomStringUtils.randomNumeric(4), 60, mobile);
        log.info(smsCode.getCode());
        // 调用短信网关 这里只做模拟
        System.out.println(smsCode.getCode());
        session.setAttribute(MyContants.SMS_SESSION_KEY,smsCode);

        return AjaxResponse.success("短信验证码已发送");
    }
}
