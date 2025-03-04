package com.dev.LandingBuilder.config;

import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import jakarta.servlet.http.HttpServletRequest;

@Configuration
public class CustomCorsConfiguration {

    // 허용할 프론트엔드 도메인 추가
    private static final List<String> ALLOWED_DOMAINS = List.of(
        "https://jiwooclinic.co.kr",
        "https://jiwooclinic.co.kr/landing/jp"
    );

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();

        // 여러 개의 도메인을 허용
        config.setAllowedOrigins(ALLOWED_DOMAINS); 
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS")); // OPTIONS 추가
        config.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        config.setAllowCredentials(true); // 쿠키/세션 인증 허용

        source.registerCorsConfiguration("/**", config);
        return new DynamicCorsConfigurationSource(source);
    }

    /**
     * 특정 도메인에서 온 요청만 CORS 허용 (그 외 도메인은 차단)
     */
    private static class DynamicCorsConfigurationSource implements CorsConfigurationSource {
        private final UrlBasedCorsConfigurationSource delegate;

        public DynamicCorsConfigurationSource(UrlBasedCorsConfigurationSource delegate) {
            this.delegate = delegate;
        }

        @Override
        public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {
            String origin = request.getHeader(HttpHeaders.ORIGIN);

            // ALLOWED_DOMAINS 리스트에 있는 도메인만 허용
            if (origin != null && ALLOWED_DOMAINS.contains(origin)) {
                return delegate.getCorsConfiguration(request);
            }
            return null; // 다른 도메인은 CORS 차단
        }
    }
}
