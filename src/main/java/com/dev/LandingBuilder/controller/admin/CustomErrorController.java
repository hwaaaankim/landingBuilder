package com.dev.LandingBuilder.controller.admin;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;

@Controller
public class CustomErrorController implements ErrorController{

	private String VIEW_PATH = "/error/";
	
	@GetMapping("/error")
    public String handleError(HttpServletRequest request) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);

        if (status != null) {
            int statusCode = Integer.parseInt(status.toString());

            if (statusCode == HttpStatus.NOT_FOUND.value()) {
                return VIEW_PATH + "404"; // 404 페이지
            } else if (statusCode == HttpStatus.FORBIDDEN.value()) {
                return VIEW_PATH + "403"; // 403 페이지
            } else {
                return VIEW_PATH + "500"; // 500 페이지
            }
        }
        return "error"; // 기본 에러 페이지
    }
}
