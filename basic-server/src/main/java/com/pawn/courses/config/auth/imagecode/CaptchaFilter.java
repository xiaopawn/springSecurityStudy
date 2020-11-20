package com.pawn.courses.config.auth.imagecode;

import com.pawn.courses.config.auth.MyAuthenticationFailureHandler;
import com.pawn.courses.utils.MyContants;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.session.SessionAuthenticationException;
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
public class CaptchaFilter extends OncePerRequestFilter {

    @Resource
    private MyAuthenticationFailureHandler myAuthenticationFailureHandler;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {

        if (StringUtils.equals("/login",request.getRequestURI())
                && StringUtils.endsWithIgnoreCase(request.getMethod(),"post")){

            try {
                validate(new ServletWebRequest(request));
            }catch (AuthenticationException authenticationException){

                myAuthenticationFailureHandler.onAuthenticationFailure(request,response,authenticationException);
                // 不去执行下面的过滤器链
                return;
            }
        }

        // 成功之后放行
        chain.doFilter(request,response);
    }

    private void validate(ServletWebRequest request) throws ServletRequestBindingException {

        HttpSession session = request.getRequest().getSession();

/*        String captchaCode = ServletRequestUtils.getStringParameter(
                request.getRequest(),"captchaCode");*/
        String codeInRequest = request.getParameter("captchaCode");

        if (StringUtils.isEmpty(codeInRequest)){
            throw new SessionAuthenticationException("验证码不能为空");
        }
        CaptchaCode codeInSession = (CaptchaCode) session.getAttribute(MyContants.CAPTCHA_SESSION_KEY);

        if (Objects.isNull(codeInSession)){
            throw new SessionAuthenticationException("验证码不存在");
        }

        if (codeInSession.isExpired()){
            session.removeAttribute(MyContants.CAPTCHA_SESSION_KEY);
            throw new SessionAuthenticationException("验证码已经过期");
        }

        // 5. 请求验证码校验
        if(!StringUtils.equals(codeInSession.getCode(), codeInRequest)) {
            throw new SessionAuthenticationException("验证码不匹配");
        }

    }
}
