package com.pawn.jwtserver.config.jwt;

import com.pawn.commons.exception.CustomException;
import com.pawn.commons.exception.CustomExceptionType;
import com.pawn.jwtserver.config.auth.exception.AjaxResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Map;

@RestController
public class JwtAuthController {

    @Resource
    private JwtAuthService jwtAuthService;

    @PostMapping("authentication")
    public AjaxResponse login(@RequestBody Map<String,Object> params){

        String username = (String) params.get("username");
        String password = (String) params.get("password");

        if (StringUtils.isEmpty(username) || StringUtils.isEmpty(password)){
            return AjaxResponse.error(new CustomException(CustomExceptionType.USER_INPUT_ERROR,"用户名密码不能为空"));
        }

        try {
            String token = jwtAuthService.login(username, password);
            return AjaxResponse.success(token);
        }catch (CustomException e){
            return AjaxResponse.error(e);
        }

    }

    @GetMapping("refreshToken")
    public AjaxResponse refresh(@RequestHeader("${jwt.header}") String token){

        return AjaxResponse.success(jwtAuthService.refreshToken(token));
    }
}
