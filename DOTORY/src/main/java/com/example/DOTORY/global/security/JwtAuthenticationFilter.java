package com.example.DOTORY.global.security;

import com.example.DOTORY.global.jwt.JwtProvider;
import com.example.DOTORY.user.domain.entity.UserEntity;
import com.example.DOTORY.user.domain.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;

    private final UserRepository userRepository;

    public JwtAuthenticationFilter(JwtProvider jwtProvider, UserRepository userRepository) {
        this.userRepository = userRepository;
        this.jwtProvider = jwtProvider;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String token = resolveToken(request);
        if (token != null && jwtProvider.validateToken(token)) {
            String userId = jwtProvider.getUserIdFromToken(token);

            // DB에서 UserEntity 조회
            UserEntity user = userRepository.findByUserID(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // CustomUserPrincipal 생성
            CustomUserPrincipal principal = new CustomUserPrincipal(user);
            UsernamePasswordAuthenticationToken auth =
                    new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(auth);
        }
        filterChain.doFilter(request, response);
    }


    private String resolveToken(HttpServletRequest request) {
        String bearer = request.getHeader("Authorization");
        if (bearer != null && bearer.startsWith("Bearer ")) {
            return bearer.substring(7);
        }
        return null;
    }
}
