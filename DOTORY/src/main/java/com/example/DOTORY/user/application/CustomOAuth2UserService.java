package com.example.DOTORY.user.application;

import com.example.DOTORY.user.domain.entity.UserEntity;
import com.example.DOTORY.global.security.CustomUserPrincipal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final SNSLoginService snsLoginService;

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = new DefaultOAuth2UserService().loadUser(userRequest);

        log.info("oAuth2User attributes: " + oAuth2User.getAttributes());

        String registrationId = userRequest.getClientRegistration().getRegistrationId(); // kakao or naver


        String providerID = null;
        String email = null;
        String userName = null;

        if ("kakao".equals(registrationId)) {
            // 카카오 사용자 정보 구조
            Long id = oAuth2User.getAttribute("id");  // Long 타입
            providerID = String.valueOf(id);          // String으로 변환

            Map<String, Object> kakaoAccount = oAuth2User.getAttribute("kakao_account");
            if (kakaoAccount != null) {
                email = (String) kakaoAccount.get("email");

                Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");
                if (profile != null) {
                    userName = (String) profile.get("nickname");
                }
            }

        } else if ("naver".equals(registrationId)) {
            // 네이버 사용자 정보 구조
            Map<String, Object> response = oAuth2User.getAttribute("response");
            if (response != null) {
                providerID = (String) response.get("id");
                email = (String) response.get("email");
                userName = (String) response.get("name");
            }

        } else {
            throw new OAuth2AuthenticationException("지원하지 않는 SNS입니다.");
        }

        // SNS 로그인 처리
        UserEntity user;
        switch (registrationId) {
            case "kakao":
                user = snsLoginService.loginWithKakao(providerID, email, userName);
                break;
            case "naver":
                user = snsLoginService.loginWithNaver(providerID, email, userName);
                break;
            default:
                throw new OAuth2AuthenticationException("지원하지 않는 SNS입니다.");
        }

        user.getUserRole();
        if (providerID == null || providerID.isEmpty()) {
            throw new OAuth2AuthenticationException("SNS에서 ID를 가져올 수 없습니다. registrationId=" + registrationId);
        }

        Map<String, Object> attributes = Map.of(
                "id", providerID,
                "email", email != null ? email : "",
                "nickname", userName != null ? userName : ""
        );
        return new CustomUserPrincipal(user, attributes);

    }
}
