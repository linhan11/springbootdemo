package jp.co.saison.tvc.springbootdemo.app;

import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    private DataSource dataSource;

    @Autowired
    MyLogoutHandler myLogoutHandler;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .authorizeRequests()
                .antMatchers("/css/**", "/js/**", "/users/**", "/img/**", "/api/**").permitAll()
                .anyRequest().authenticated()
                .and()
            .formLogin()
                .loginPage("/login")
                .permitAll()
                .and()
            .logout()
	            .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
	            .logoutSuccessUrl("/login")
	            .addLogoutHandler(myLogoutHandler)
	            .invalidateHttpSession(true)
	            .deleteCookies("JSESSIONID")
                .permitAll();

        /**
         * CSRF適用URL判断クラス
         */

        RequestMatcher csrfRequestMatcher = new RequestMatcher() {

            private AntPathRequestMatcher disabledRequestMatcher =
                    new AntPathRequestMatcher("/api/**");

            @Override
            public boolean matches(HttpServletRequest request) {

                // GETならCSRFのチェックはしない
                if("GET".equals(request.getMethod())) {
                    return false;
                }

                // 特定のURLに該当する場合、CSRFチェックしない
                return !disabledRequestMatcher.matches(request);
            }

        };
        http.csrf().requireCsrfProtectionMatcher(csrfRequestMatcher);

    }
/*
    @Bean
    @Override
    public UserDetailsService userDetailsService() {
        UserDetails user =
             User.withDefaultPasswordEncoder()
                .username("user")
                .password("password")
                .roles("USER")
                .build();

        return new InMemoryUserDetailsManager(user);
    }
    */
    @Autowired
    public void configAuthentication(AuthenticationManagerBuilder auth) throws Exception {
        auth.jdbcAuthentication()
                .dataSource(dataSource)
                .passwordEncoder(passwordEncoder())
                .usersByUsernameQuery(
                        "select name as username, password, true from demo_user where name = ?")
                .authoritiesByUsernameQuery(
                		"select name as username, 'ROLE_USER' from demo_user where name = ?");

     }

	@Bean
	public PasswordEncoder passwordEncoder(){
		PasswordEncoder encoder = new BCryptPasswordEncoder();
		return encoder;
	}
}