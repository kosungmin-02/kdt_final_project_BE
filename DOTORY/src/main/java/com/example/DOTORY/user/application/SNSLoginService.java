package com.example.DOTORY.user.application;

import com.example.DOTORY.user.domain.entity.*;
import com.example.DOTORY.user.domain.repository.UserRepository;
import com.example.DOTORY.user.domain.repository.UserSNSRepository;
import com.example.DOTORY.global.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class SNSLoginService {

    private final UserRepository userRepository;
    private final UserSNSRepository userSNSRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    public UserEntity loginWithKakao(String providerID, String email, String userName) {
        // 1. provider + providerID 기준으로 조회
        Optional<UserSNSEntity> optionalSNS =
                userSNSRepository.findByProviderAndProviderID(UserSNSEntity.Provider.KAKAO, providerID);

        log.info("optionalSNS.isPresent() = {}", optionalSNS.isPresent());
        if (optionalSNS.isPresent()) {
            return optionalSNS.get().getUser();
        }

        // 2. provider 기반 새로운 User 생성 (이메일 중복 가능)
        UserEntity user = UserEntity.builder()
                .userID("kakao_" + providerID)
                .userPassword(passwordEncoder.encode(UUID.randomUUID().toString()))
                .userEmail(email) // 같은 이메일 가능
                .userName(userName)
                .userNickname(userName)
                .userRole(UserRole.USER)
                .userLogin(UserLogin.LOGIN)
                .userStatus(UserStatus.ACTIVE)
                .build();

        user = userRepository.save(user);

        // 3. SNS 정보 연결
        UserSNSEntity snsEntity = UserSNSEntity.builder()
                .user(user)
                .provider(UserSNSEntity.Provider.KAKAO)
                .providerID(providerID)
                .build();

        userSNSRepository.save(snsEntity);

        return user;
    }

    public UserEntity loginWithNaver(String providerID, String email, String userName) {
        Optional<UserSNSEntity> optionalSNS =
                userSNSRepository.findByProviderAndProviderID(UserSNSEntity.Provider.NAVER, providerID);

        if (optionalSNS.isPresent()) {
            return optionalSNS.get().getUser();
        }

        UserEntity user = UserEntity.builder()
                .userID("naver_" + providerID)
                .userPassword(passwordEncoder.encode(UUID.randomUUID().toString()))
                .userEmail(email)
                .userName(userName)
                .userNickname(userName)
                .userRole(UserRole.USER)
                .userLogin(UserLogin.LOGIN)
                .userStatus(UserStatus.ACTIVE)
                .build();

        user = userRepository.save(user);

        UserSNSEntity snsEntity = UserSNSEntity.builder()
                .user(user)
                .provider(UserSNSEntity.Provider.NAVER)
                .providerID(providerID)
                .build();

        userSNSRepository.save(snsEntity);

        return user;
    }

    @Transactional
    public String loginWithKakaoAndReturnJwt(String providerID, String email, String userName) {
        UserEntity user = loginWithKakao(providerID, email, userName);
        return jwtProvider.generateToken(user.getUserID());
    }

    @Transactional
    public String loginWithNaverAndReturnJwt(String providerID, String email, String userName) {
        UserEntity user = loginWithNaver(providerID, email, userName);
        return jwtProvider.generateToken(user.getUserID());
    }
}
