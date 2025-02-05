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
	SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
		
		http.csrf((csrfConfig) -> 
				csrfConfig.disable())
			.headers((headerConfig) -> 
					headerConfig
						.frameOptions(frameOptionConfig -> frameOptionConfig.disable()))
			.authorizeHttpRequests((authorizeReqeust) -> 
				authorizeReqeust
					.requestMatchers(adminUrls).hasAnyAuthority("ROLE_ADMIN", "ROLE_GUEST")
					.requestMatchers(visitorUrls).permitAll()
					.anyRequest().permitAll()					
			)
			.formLogin((formLogin) -> 
				formLogin
					.defaultSuccessUrl("/admin/insertClientForm", false)
			)
			.logout((logout) ->
				logout
					.logoutUrl("/logout")
					.deleteCookies("JSESSIONID")
					.invalidateHttpSession(true)
					.logoutSuccessUrl("/")
			);
		
		return http.build();
			
	}
}





















