package com.pawn.jwtserver.config;

import com.pawn.jwtserver.config.auth.MyUserDetailsService;
import com.pawn.jwtserver.config.auth.MylogoutSuccessHandler;
import com.pawn.jwtserver.config.jwt.JwtAuthenticationTokenFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.util.Arrays;

@Configuration
// 开启方法级别的权限控制
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Resource
    private MyUserDetailsService myUserDetailsService;

    @Resource
    private MylogoutSuccessHandler mylogoutSuccessHandler;

    @Resource
    private DataSource dataSource;

    @Resource
    private JwtAuthenticationTokenFilter jwtAuthenticationTokenFilter;


    // 有两种认证方式httpBasic认证  formLogin认证
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // 开启跨站点伪造攻击的防御 把令牌放到token里面
        http.csrf().csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                .ignoringAntMatchers("/authentication")
                // 开启跨域访问
                .and().cors().and()
                .addFilterBefore(jwtAuthenticationTokenFilter, UsernamePasswordAuthenticationFilter.class).
                logout()
                .logoutUrl("/signUrl").
                // logoutSuccessUrl 和 logoutSuccessHandler只能用一个 否则后者将会失效
//                .logoutSuccessUrl("/login.html").
                // logoutSuccessHandler可以做一些复杂的业务逻辑 例如登陆时间的统计
                logoutSuccessHandler(mylogoutSuccessHandler).
                deleteCookies("JSESSIONID").
                and().
                rememberMe().
                rememberMeParameter("remember-me-new").
                rememberMeCookieName("remember-me-cookie").
                tokenValiditySeconds(2 * 24 * 60 * 60).
                // 将记住密码功能添加到数据库中
                tokenRepository(persistentTokenRepository()).
                and()
                // 之前不关闭csrf会有很多请求访问不了
                //.csrf().disable()
                .authorizeRequests()
                .antMatchers("/authentication","/refreshToken").permitAll()
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
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

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

    @Bean
    public PersistentTokenRepository persistentTokenRepository(){

        JdbcTokenRepositoryImpl jdbcTokenRepository = new JdbcTokenRepositoryImpl();
        jdbcTokenRepository.setDataSource(dataSource);

        return jdbcTokenRepository;

    }

    @Bean(name = BeanIds.AUTHENTICATION_MANAGER)
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    // springSecurity推荐的配置跨域访问策略
    @Bean
    CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:8888"));
        configuration.setAllowedMethods(Arrays.asList("GET","POST"));
        configuration.applyPermitDefaultValues();

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}