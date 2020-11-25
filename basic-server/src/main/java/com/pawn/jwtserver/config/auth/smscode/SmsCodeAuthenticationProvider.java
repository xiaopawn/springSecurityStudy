package com.pawn.jwtserver.config.auth.smscode;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.Objects;

public class SmsCodeAuthenticationProvider implements AuthenticationProvider {

    private UserDetailsService userDetailsService;

    public void setUserDetailsService(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        SmsCodeAuthenticationToken authenticationToken = (SmsCodeAuthenticationToken)authentication;
        // 当时经过此过滤器放入手机号
        UserDetails userDetails = userDetailsService.loadUserByUsername((String)authenticationToken.getPrincipal());
        if (Objects.isNull(userDetails)){
            throw new InternalAuthenticationServiceException("无法根据手机号获取用户信息");
        }
        // 设置用户信息
        SmsCodeAuthenticationToken smsCodeAuthenticationToken = new SmsCodeAuthenticationToken(userDetails,userDetails.getAuthorities());
        smsCodeAuthenticationToken.setDetails(authenticationToken.getDetails());
        return smsCodeAuthenticationToken;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return SmsCodeAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
