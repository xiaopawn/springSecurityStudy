package com.pawn.jwtserver.config.jwt;

import com.pawn.jwtserver.config.auth.MyUserDetailsService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.annotation.Resource;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;

@Component
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {

    @Resource
    private JwtTokenUtils jwtTokenUtils;

    @Resource
    private MyUserDetailsService myUserDetailsService;


    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {

        String token = request.getHeader(jwtTokenUtils.getHeader());
        if (StringUtils.isNotEmpty(token)){
            String username = jwtTokenUtils.getUsernameFromToken(token);

            if (StringUtils.isNotBlank(username) &&
                    Objects.isNull(SecurityContextHolder.getContext().getAuthentication())){

                UserDetails userDetails = myUserDetailsService.loadUserByUsername(username);
                if (jwtTokenUtils.validateToken(token,userDetails)){

                    // 没有过期
                    // 给使用jwt的用户进行赋权
                    UsernamePasswordAuthenticationToken authenticationToken =
                            new UsernamePasswordAuthenticationToken(userDetails,null,userDetails.getAuthorities());

                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                }
            }
        }

        chain.doFilter(request,response);

    }
}
