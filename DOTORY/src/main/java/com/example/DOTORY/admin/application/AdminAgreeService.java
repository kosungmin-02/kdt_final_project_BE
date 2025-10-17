package com.example.DOTORY.admin.application;

import com.example.DOTORY.admin.api.dto.UserInfoDTO;
import com.example.DOTORY.global.code.status.ErrorStatus;
import com.example.DOTORY.global.exception.GeneralException;
import com.example.DOTORY.user.api.dto.AgreeDTO;
import com.example.DOTORY.user.domain.entity.AgreeEntity;
import com.example.DOTORY.user.domain.entity.AgreeType;
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

    public List<AgreeDTO> getAllAgree() {
        return agreeRepository.findAll().stream()
                .map(AgreeDTO::new)
                .collect(Collectors.toList());
    }

    @Transactional
    public AgreeDTO updateAgree(int agreeId, String title, String content, AgreeType type) {
        AgreeEntity agree = agreeRepository.findById(agreeId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.RESOURCE_NOT_FOUND, "존재하지 않는 약관입니다."));

        agree.setAgreeTitle(title);
        agree.setAgreeContent(content);
        agree.setAgreeType(type);

        AgreeEntity saved = agreeRepository.save(agree);
        return new AgreeDTO(saved);
    }

    public List<UserInfoDTO> getUsersAgreedForOptional(int agreeId) {
        AgreeEntity agree = agreeRepository.findById(agreeId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.RESOURCE_NOT_FOUND, "존재하지 않는 약관입니다."));

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

    @Transactional
    public void deleteAgree(int agreeId) {
        AgreeEntity agree = agreeRepository.findById(agreeId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.RESOURCE_NOT_FOUND, "존재하지 않는 약관입니다."));

        userAgreeRepository.deleteAllByAgree_AgreeID(agreeId);
        userAgreeRepository.flush();

        agreeRepository.delete(agree);
        agreeRepository.flush();
    }

    @Transactional
    public AgreeDTO createAgree(String title, String content, AgreeType type) {
        // 이미 동일한 제목의 약관이 존재하는지 체크 (중복 방지용)
        boolean exists = agreeRepository.existsByAgreeTitle(title);
        if (exists) {
            throw new GeneralException(ErrorStatus.CONFLICT, "이미 존재하는 약관 제목입니다.");
        }

        // 새 약관 엔티티 생성
        AgreeEntity newAgree = new AgreeEntity();
        newAgree.setAgreeTitle(title);
        newAgree.setAgreeContent(content);
        newAgree.setAgreeType(type);

        // 저장
        AgreeEntity saved = agreeRepository.save(newAgree);

        return new AgreeDTO(saved);
    }

}