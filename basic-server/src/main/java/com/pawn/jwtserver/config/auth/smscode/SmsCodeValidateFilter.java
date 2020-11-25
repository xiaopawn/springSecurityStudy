package com.pawn.jwtserver.config.auth.smscode;

import com.pawn.jwtserver.config.auth.MyAuthenticationFailureHandler;
import com.pawn.jwtserver.config.auth.MyUserDetails;
import com.pawn.jwtserver.config.auth.MyUserDetailsServiceMapper;
import com.pawn.jwtserver.utils.MyContants;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.annotation.Resource;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Objects;

@Component
public class SmsCodeValidateFilter extends OncePerRequestFilter {

    @Resource
    private MyAuthenticationFailureHandler myAuthenticationFailureHandler;

    @Resource
    private MyUserDetailsServiceMapper myUserDetailsServiceMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {

        if (StringUtils.equals("/smslogin",request.getRequestURI())
                && StringUtils.endsWithIgnoreCase(request.getMethod(),"post")){

            try {
                validate(new ServletWebRequest(request));
            }catch (AuthenticationException authenticationException){

                myAuthenticationFailureHandler.onAuthenticationFailure(request,response,authenticationException);
                // 不去执行下面的过滤器链 直接return结果
                return;
            }
        }

        // 成功之后放行
        chain.doFilter(request,response);
    }

    private void validate(ServletWebRequest request) throws ServletRequestBindingException {

        String mobile = request.getParameter("mobile");
        if (StringUtils.isEmpty(mobile)){
            throw new SmsCodeException("手机号码不能为空");
        }
        HttpSession session = request.getRequest().getSession();

/*        String captchaCode = ServletRequestUtils.getStringParameter(
                request.getRequest(),"captchaCode");*/
        String codeInRequest = request.getParameter("smsCode");

        if (StringUtils.isEmpty(codeInRequest)){
            throw new SmsCodeException("验证码不能为空");
        }

        SmsCode smsCodeInSession = (SmsCode) session.getAttribute(MyContants.SMS_SESSION_KEY);

        if (Objects.isNull(smsCodeInSession)){
            throw new SmsCodeException("验证码不存在");
        }

        if (!StringUtils.equals(smsCodeInSession.getMobile(),mobile)){
            throw new SmsCodeException("短信发送目标与您输入的手机号不一致");
        }

        if (smsCodeInSession.isExpired()){
            session.removeAttribute(MyContants.SMS_SESSION_KEY);
            throw new SmsCodeException("验证码已经过期");
        }

        // 5. 请求验证码校验
        if(!StringUtils.equals(smsCodeInSession.getCode(), codeInRequest)) {
            throw new SmsCodeException("验证码不匹配");
        }

        MyUserDetails userDetails = myUserDetailsServiceMapper.findByUserName(mobile);
        if (Objects.isNull(userDetails)){
            throw new SmsCodeException("该用户不是系统注册用户");
        }

        session.removeAttribute(MyContants.SMS_SESSION_KEY);
    }
}
