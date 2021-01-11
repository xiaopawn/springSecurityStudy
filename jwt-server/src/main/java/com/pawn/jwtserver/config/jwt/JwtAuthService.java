package com.pawn.jwtserver.config.jwt;

import com.pawn.commons.exception.CustomException;
import com.pawn.commons.exception.CustomExceptionType;
import com.pawn.jwtserver.config.auth.MyUserDetailsService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class JwtAuthService {

    @Resource
    private AuthenticationManager authenticationManager;

    @Resource
    private MyUserDetailsService myUserDetailsService;

    @Resource
    private JwtTokenUtils jwtTokenUtils;

    // 登陆认证换取jwt令牌
    public String login(String username,String password){

        UsernamePasswordAuthenticationToken upToken = new UsernamePasswordAuthenticationToken(username,password);

        try {
            Authentication authenticate = authenticationManager.authenticate(upToken);
            SecurityContextHolder.getContext().setAuthentication(authenticate);
        }catch (AuthenticationException e){
            throw new CustomException(CustomExceptionType.USER_INPUT_ERROR,"用户名密码错误");
        }

        UserDetails userDetails = myUserDetailsService.loadUserByUsername(username);


        return jwtTokenUtils.generateToken(userDetails);
    }

    public String refreshToken(String oldToken){
        if (!jwtTokenUtils.isTokenExpired(oldToken)){
            return jwtTokenUtils.refreshToken(oldToken);
        }

        return null;
    }
}
