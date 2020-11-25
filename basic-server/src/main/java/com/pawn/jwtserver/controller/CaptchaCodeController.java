package com.pawn.jwtserver.controller;

import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.pawn.jwtserver.config.auth.imagecode.CaptchaCode;
import com.pawn.jwtserver.utils.MyContants;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.IOException;

@Controller
public class CaptchaCodeController {

    @Resource
    private DefaultKaptcha captchaProducer;

    @GetMapping("getCaptchaCode")
    public void getCaptchaCode(HttpSession session, HttpServletResponse response) throws IOException {

        response.setDateHeader("Expires", 0);
        response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
        response.addHeader("Cache-Control", "post-check=0, pre-check=0");
        response.setHeader("Pragma", "no-cache");
        response.setContentType("image/jpeg");
        String captchaCode = this.captchaProducer.createText();

        // 生成验证码 并设置过期时间
        session.setAttribute(MyContants.CAPTCHA_SESSION_KEY,new CaptchaCode(captchaCode,60));

        try(ServletOutputStream out = response.getOutputStream()) {

            BufferedImage imageCode = captchaProducer.createImage(captchaCode);
            ImageIO.write(imageCode,"jpg",out);
        }

    }
}
