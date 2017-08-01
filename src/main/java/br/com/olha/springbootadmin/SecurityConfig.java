package br.com.olha.springbootadmin;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.formLogin().loginPage("/login.html").loginProcessingUrl("/login").permitAll();
        http.authorizeRequests().antMatchers(HttpMethod.POST, "/api/applications").permitAll();
        http.authorizeRequests().antMatchers("/mgmt/health").permitAll();

        http.logout().logoutUrl("/logout");
        http.csrf().disable();

        http.authorizeRequests()
                .antMatchers("/login.html", "/**/*.css", "/img/**", "/third-party/**")
                .permitAll();
        http.authorizeRequests().antMatchers("/**").authenticated();

        http.httpBasic();
    }
}
