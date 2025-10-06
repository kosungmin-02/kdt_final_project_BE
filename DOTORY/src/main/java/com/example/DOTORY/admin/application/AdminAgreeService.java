package com.example.DOTORY.admin.application;

import com.example.DOTORY.admin.api.dto.UserAgreeInfoDTO;
import com.example.DOTORY.admin.api.dto.UserInfoDTO;
import com.example.DOTORY.user.api.dto.AgreeDTO;
import com.example.DOTORY.user.domain.entity.AgreeEntity;
import com.example.DOTORY.user.domain.entity.AgreeType;
import com.example.DOTORY.user.domain.entity.UserAgreeEntity;
import com.example.DOTORY.user.domain.entity.UserEntity;
import com.example.DOTORY.user.domain.repository.AgreeRepository;
import com.example.DOTORY.user.domain.repository.UserAgreeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminAgreeService {

    private final AgreeRepository agreeRepository;
    private final UserAgreeRepository userAgreeRepository;

    // 전체 약관 목록 조회하기
    public List<AgreeDTO> getAllAgree() {
        return agreeRepository.findAll().stream()
                .map(AgreeDTO::new)
                .collect(Collectors.toList());
    }

    // 약관 내용 수정 가능
    @Transactional
    public AgreeDTO updateAgree(int agreeId, String title, String content, AgreeType type) {
        AgreeEntity agree = agreeRepository.findById(agreeId)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 약관입니다."));

        agree.setAgreeTitle(title);
        agree.setAgreeContent(content);
        agree.setAgreeType(type);

        // save 하면 변경일 자동으로 갱신하기
        AgreeEntity saved = agreeRepository.save(agree);
        return new AgreeDTO(saved);
    }



    // 어떤 약관에 어떤 사용자들이 동의 했는지 확인하기.
    public List<UserInfoDTO> getUsersAgreedForOptional(int agreeId) {
        AgreeEntity agree = agreeRepository.findById(agreeId)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 약관입니다."));

        if (!AgreeType.OPTIONAL.equals(agree.getAgreeType())) {
            return List.of();
        }


        return userAgreeRepository.findByAgree_AgreeIDAndAgreedTrue(agreeId)
                .stream()
                .map(userAgree -> new UserInfoDTO(
                        userAgree.getUser().getUserPK(),
                        userAgree.getUser().getUserName(),
                        userAgree.getUser().getUserNickname(),
                        userAgree.getUser().getUserEmail()
                ))
                .collect(Collectors.toList());
    }


}
