package com.pawn.jwtserver.config.auth;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Component("rbacService")
public class MyRBACService {

    private final AntPathMatcher antPathMatcher = new AntPathMatcher();
    @Resource
    private MyRBACServiceMapper myRBACServiceMapper;

    public boolean hasPermission(HttpServletRequest request, Authentication authentication){

        Object principal = authentication.getPrincipal();

        if(principal instanceof UserDetails){
            UserDetails userDetails = ((UserDetails)principal);

            String username = userDetails.getUsername();
            List<String> urls = myRBACServiceMapper.findUrlsByUserName(username);
            return urls.stream().anyMatch(
                    url -> antPathMatcher.match(url,request.getRequestURI())
            );
        }
        return false;
    }
}
