package com.dev.LandingBuilder.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

	@Value("${upload.path}") // 현재 활성화된 프로파일의 값 적용
    private String uploadPath;
	
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // '/administration/upload/**'로 요청이 오면 'D:/upload/'에서 파일을 제공
        registry.addResourceHandler("/administration/upload/**")
                .addResourceLocations("file:" + uploadPath + "/")
                .setCachePeriod(3600); // 캐시 유지 시간(초)
        registry.addResourceHandler("/front/**")
        .addResourceLocations("classpath:/static/front/");

		// favicon.ico를 정적 리소스로 처리
		registry.addResourceHandler("/favicon.ico")
		        .addResourceLocations("classpath:/static/");
    }
}
