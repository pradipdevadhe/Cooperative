package com.cooperative.spring.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationTrustResolver;
import org.springframework.security.authentication.AuthenticationTrustResolverImpl;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableWebSecurity
public class SpringSecurityConfiguration extends WebSecurityConfigurerAdapter {
	
	@Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .formLogin().loginPage("/login")
            .loginProcessingUrl("/login").usernameParameter("usmUserName").passwordParameter("usmUserPass")
            .and()
            .authorizeRequests().antMatchers("/static/**").access("permitAll")
            .and()
            .rememberMe().rememberMeParameter("remember-me")
            .tokenValiditySeconds(86400)
            .and()
            .csrf()
            .and()
            .exceptionHandling().accessDeniedPage("/Access_Denied");
        
    }

	@Bean
	public AuthenticationTrustResolver getAuthenticationTrustResolver() {
		return new AuthenticationTrustResolverImpl();
	}
}
