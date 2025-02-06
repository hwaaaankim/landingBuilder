package com.dev.LandingBuilder.config;

import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.dev.LandingBuilder.handler.CustomLoginSuccessHandler;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class WebSecurityConfig {

	private final String[] visitorUrls = {
			"/**",
			"/administration/**",
			"/front/**",
			"/api/v1/**"
	};
	
	private final String[] adminUrls = {
		"/admin/**"	
	};
	
	@Bean
	WebSecurityCustomizer webSecurityCustomizer() {
		return (web) -> web.ignoring().requestMatchers(PathRequest.toStaticResources().atCommonLocations());
	}
	
	@Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
	
	@Bean
	SecurityFilterChain filterChain(HttpSecurity http, CustomLoginSuccessHandler customLoginSuccessHandler) throws Exception{
		
		http.csrf(csrfConfig -> csrfConfig.disable())
	        .headers(headerConfig -> 
	            headerConfig.frameOptions(frameOptionConfig -> frameOptionConfig.disable()))
	        .authorizeHttpRequests(authorizeRequests -> 
	            authorizeRequests
	            	// GUEST 유저는 /admin/clientManager, /admin/clientDetail만 접근 가능
		            .requestMatchers("/admin/clientManager", "/admin/clientDetail/**").hasAnyAuthority("ROLE_GUEST", "ROLE_ADMIN")
		            // ADMIN 유저는 모든 /admin/** 접근 가능
		            .requestMatchers(adminUrls).hasAuthority("ROLE_ADMIN")
	                // 일반 방문자 URL 허용
	                .requestMatchers(visitorUrls).permitAll()
	                // 나머지 모든 요청은 인증 필요 없음
	                .anyRequest().permitAll()
	        )
	        .formLogin(formLogin -> 
	            formLogin
	            	.defaultSuccessUrl("/admin/insertClientForm", false)
	            	.successHandler(customLoginSuccessHandler)	
	        )
	        .logout(logout -> 
	            logout.logoutUrl("/logout")
	                  .deleteCookies("JSESSIONID")
	                  .invalidateHttpSession(true)
	                  .logoutSuccessUrl("/")
	        );
	
	    return http.build();
			
	}
}





















