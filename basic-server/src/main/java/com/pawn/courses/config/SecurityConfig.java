package com.pawn.courses.config;

import com.pawn.courses.config.auth.MyAuthenticationSuccessHandler;
import com.pawn.courses.config.auth.MyExpiredSessionStrategy;
import com.pawn.courses.config.auth.MyUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.annotation.Resource;

@Configuration
// 开启方法级别的权限控制
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Resource
    private MyUserDetailsService myUserDetailsService;

    @Resource
    private MyAuthenticationSuccessHandler myAuthenticationSuccessHandler;

    // 有两种认证方式httpBasic认证  formLogin认证
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.rememberMe().
                rememberMeParameter("remember-me-new").
                rememberMeCookieName("remember-me-cookie").
                tokenValiditySeconds(2 * 24 * 60 * 60).
                and().
                csrf().disable().formLogin()//开启formLogin认证
                .loginPage("/login.html")
                .loginProcessingUrl("/login")
                .usernameParameter("username")
                .passwordParameter("password")
                // 登陆成功默认跳转的url 资源路径
//                .defaultSuccessUrl("/index")
                .successHandler(myAuthenticationSuccessHandler)
                .and()
                .authorizeRequests()
                .antMatchers("/login.html","/login", "/kaptcha","/smscode","/smslogin").permitAll()
                .antMatchers("/index").authenticated()
                .anyRequest().access("@rbacService.hasPermission(request,authentication)")
                // <---------------之前的静态加载start-------------------->
                // hasAnyAuthority 如果是ROLE_user 等同于 hasAnyRole 前面默认加了一个ROLE_
                // 配置规则 给user admin赋予页面的访问权限
           /*     .antMatchers("/index","/biz1","/biz2").hasAnyAuthority("ROLE_user","ROLE_admin")
                // 配置规则 给admin赋予 admin页面的访问权限
                //.antMatchers("/syslog","/sysuser").hasAnyRole("admin")
                .antMatchers("/syslog").hasAuthority("/syslog")
                .antMatchers("/sysuser").hasAuthority("/sysuser")
                .anyRequest()
                //所有请求都需要登录认证才能访问
                .authenticated()*/
                // <---------------之前的静态加载end-------------------->

                .and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                .invalidSessionUrl("/login.html")
                .sessionFixation().migrateSession()
                .maximumSessions(1)
                .maxSessionsPreventsLogin(false)
                .expiredSessionStrategy(new MyExpiredSessionStrategy())
        ;

    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {

    /*    auth.inMemoryAuthentication()
                .withUser("user").password(passwordEncoder().encode("123456")).roles("user")
                .and()
                .withUser("admin").password(passwordEncoder().encode("123456")).authorities("sys:log", "sys:user")
                .and()
                // 配置BCtypt加密算法
                .passwordEncoder(passwordEncoder());*/

        // 自定义用户权限配置
        auth.userDetailsService(myUserDetailsService)
                .passwordEncoder(passwordEncoder());
    }

/*    public static void main(String[] args) {
        String encode = passwordEncoder().encode("123456");
        System.out.println(encode);
    }*/

    @Override
    public void configure(WebSecurity web) throws Exception {
        // 忽略系统静态资源 不走过滤器
        web.ignoring().antMatchers("/css/**","/fonts/&&","/img/**","/js/**");
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
}