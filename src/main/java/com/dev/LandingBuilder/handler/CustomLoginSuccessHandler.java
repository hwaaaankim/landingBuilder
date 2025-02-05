package com.dev.LandingBuilder.handler;

import java.io.IOException;
import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class CustomLoginSuccessHandler implements AuthenticationSuccessHandler {

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			org.springframework.security.core.Authentication authentication) throws IOException, ServletException {
		Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

        // GUEST 유저인 경우
        if (authorities.stream().anyMatch(auth -> auth.getAuthority().equals("ROLE_GUEST"))) {
            response.sendRedirect("/admin/clientManager");  // GUEST 전용 페이지로 이동
        } 
        // ADMIN 유저인 경우
        else if (authorities.stream().anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"))) {
            response.sendRedirect("/admin/insertClientForm"); // ADMIN 전용 페이지로 이동
        } 
        // 기본적으로 메인 페이지로 이동
        else {
            response.sendRedirect("/");
        }
		
	}
}
