package com.pawn.jwtserver.config.auth;

import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class MyUserDetailsService implements UserDetailsService {

    @Resource
    private MyUserDetailsServiceMapper myUserDetailsServiceMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        // 根据用户名查找具体的用户信息
        MyUserDetails userDetails = myUserDetailsServiceMapper.findByUserName(username);

        List<String> roleCodes = myUserDetailsServiceMapper.findRoleByUserName(username);

        List<String> authorities = myUserDetailsServiceMapper.findAuthorityByRoleCodes(roleCodes);

        // security角色必须以ROLE_开头
        roleCodes = roleCodes.stream().map(roleCode -> "ROLE_" + roleCode).collect(Collectors.toList());

        // 角色是一种特殊的权限
        authorities.addAll(roleCodes);

        userDetails.setAuthorities(AuthorityUtils.commaSeparatedStringToAuthorityList(String.join(",",authorities)));

        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        boolean matches = passwordEncoder.matches("123456", userDetails.getPassword());
        System.out.println(matches);
        return userDetails;
    }

}
