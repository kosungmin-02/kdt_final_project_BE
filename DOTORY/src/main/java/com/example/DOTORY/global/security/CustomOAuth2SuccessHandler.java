package com.example.DOTORY.global.security;

import com.example.DOTORY.user.domain.entity.UserEntity;
import com.example.DOTORY.user.domain.repository.UserRepository;
import com.example.DOTORY.global.jwt.JwtProvider;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@RequiredArgsConstructor
@Component
public class CustomOAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final UserRepository userRepository;
    private final JwtProvider jwtProvider;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        CustomUserPrincipal principal = (CustomUserPrincipal) authentication.getPrincipal();
        UserEntity user = principal.getUser();

        if (user != null) {
            // JWT 발급
            String token = jwtProvider.generateToken(user.getUserID());

            // 리액트 테스트용 - 임시 프론트 만들어서 테스트 해봤습니다.
            String redirectUrl = "http://localhost:3000/oauth/callback?token=" + token;
            response.sendRedirect(redirectUrl);
        } else {
            response.sendRedirect("http://localhost:3000/register");
        }
    }


}
