package com.pawn.courses.controller;

import com.pawn.courses.config.auth.exception.AjaxResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

@RestController
public class SmsCodeController {

    @GetMapping("getSmsCode")
    public AjaxResponse getSmsCode(@RequestParam String mobile, HttpSession session){



        return null;
    }
}
