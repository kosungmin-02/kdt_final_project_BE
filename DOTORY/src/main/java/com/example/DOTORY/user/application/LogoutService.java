package com.example.DOTORY.user.application;

import com.example.DOTORY.global.jwt.JwtProvider;
import com.example.DOTORY.user.domain.entity.UserLogin;
import com.example.DOTORY.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LogoutService {

    private final UserRepository userRepository;
    private final JwtProvider jwtProvider; // JWT에서 유저 정보 추출하기

    // 로그아웃 + 상태 업데이트
    public void logoutAndUpdateStatus(String token) {
        String jwt = token.replace("Bearer ", "");

        String userID = jwtProvider.getUserIdFromToken(jwt);       // 일반 로그인용
        String userEmail = jwtProvider.getUserEmail(jwt);          // 소셜 로그인용

        // 일반 로그인은 아이디를 사용해서 로그아웃을 한다.
        if(userID != null) {
            // 일반 로그인
            userRepository.findByUserID(userID)
                    .ifPresent(user -> {
                        user.setUserLogin(UserLogin.LOGOUT);
                        userRepository.save(user);
                    });
        } else if(userEmail != null) {

            // 소셜 로그인(카카오, 네이버)는 이메일을 사용해서 로그아웃을 한다.
            userRepository.findByUserEmail(userEmail)
                    .ifPresent(user -> {
                        user.setUserLogin(UserLogin.LOGOUT);
                        userRepository.save(user);
                    });
        } else {
            // 예외 처리
            // JWT에 ID/이메일 둘 다 없으면 로그아웃 불가
            throw new RuntimeException("JWT에서 사용자 정보를 찾을 수 없습니다.");
        }
    }

}

