package com.example.DOTORY.global.security;

import com.example.DOTORY.user.application.CustomOAuth2UserService;
import com.example.DOTORY.global.jwt.JwtProvider;
import com.example.DOTORY.user.domain.repository.UserRepository;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@RequiredArgsConstructor
@Configuration      // UserSecurityConfig가 의존성 설정이 되도록 어노테이션 부착.
@EnableWebSecurity  // Security를 지금 사용하는 웹에 적용하겠다는 의미로 어노테이션 부착.
public class UserSecurityConfig {

    private final CustomOAuth2UserService customOAuth2UserService;
    private final JwtProvider jwtProvider;
    private final CustomOAuth2SuccessHandler customOAuth2SuccessHandler;
    @Bean
    // 시큐리티 default 기능들 잠시 꺼놓기 설정
    SecurityFilterChain filterChain(HttpSecurity http, UserRepository userRepository) throws Exception{

        // CSRF와 CORS 비활성화. (SPA + API)이기 때문.
        http
                .cors(cors->cors.disable())
                .csrf(csrf -> csrf.disable());
        http
                .formLogin(login -> login.disable())
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            response.setContentType("application/json");
                            response.getWriter().write("{\"error\": \"Unauthorized\"}");
                        })
                )


                // REST API + React SPA 권한 설정
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/users/**",
                                "/oauth2/**",
                                "/api/posts/*/comments",
                                // 테스트를 위해서 관리자 페이지를 모두 허용으로 해놓았음.
                                "/api/admin/users/**",
                                "/api/posts/*/likes/count").permitAll() // 로그인/회원가입 API 허용

                        .anyRequest().authenticated() // 나머지는 인증 필요
                )

                // OAuth2 로그인 설정
                .oauth2Login(oauth2 -> oauth2
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(customOAuth2UserService)
                        )
                        .successHandler(customOAuth2SuccessHandler) // 커스텀 핸들러 연결
                );

        // JWT filter
        http.addFilterBefore(new JwtAuthenticationFilter(jwtProvider, userRepository),
                UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
