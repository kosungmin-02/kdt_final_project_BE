package com.example.DOTORY.user.application;
import com.example.DOTORY.user.api.dto.AgreeDTO;
import com.example.DOTORY.user.domain.entity.AgreeEntity;
import com.example.DOTORY.user.domain.entity.AgreeType;
import com.example.DOTORY.user.domain.entity.UserAgreeEntity;
import com.example.DOTORY.user.domain.entity.UserEntity;
import com.example.DOTORY.user.domain.repository.AgreeRepository;
import com.example.DOTORY.user.domain.repository.UserAgreeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AgreeService {

    private final AgreeRepository agreeRepository;
    private final UserAgreeRepository userAgreeRepository;

    @Autowired
    public AgreeService(AgreeRepository agreeRepository, UserAgreeRepository userAgreeRepository) {
        this.agreeRepository = agreeRepository;
        this.userAgreeRepository = userAgreeRepository;
    }

    // 필수 타입인 REQUIRED에 해당하는 약관을 가져온다.
    public List<AgreeDTO> requiredAgree() {
        List<AgreeEntity> requiredEntityList = agreeRepository.findByAgreeType(AgreeType.REQUIRED);
        return requiredEntityList.stream()
                .map(requiredEntity -> new AgreeDTO(requiredEntity))
                .collect(Collectors.toList());
    }

    // 선택 타입인 OPTIONAL에 해당하는 약관을 가져온다.
    public List<AgreeDTO> optionalAgree() {
        List<AgreeEntity> optionalEntityList = agreeRepository.findByAgreeType(AgreeType.OPTIONAL);
        return optionalEntityList.stream()
                .map(AgreeDTO::new)
                .collect(Collectors.toList());
    }


    // 사용자가 동의를 눌렀던 약관들 중에서 선택 약관만을 리스트에 저장한다.
    public List<Integer> saveOnlyOptional(List<Integer> userAgreeList) {
        // 선택약관 리스트 가져오기
        List<AgreeEntity> optionalList = agreeRepository.findByAgreeType(AgreeType.OPTIONAL);
        // 선택약관만 담을 리스트
        List<Integer> userOptionalList = new ArrayList<>();

        // 선택약관인지 확인하고서 리스트에 담기
        for(int i = 0; i<userAgreeList.size();i++){
            Integer userAgreeListID = userAgreeList.get(i);


            for(int j = 0; j < optionalList.size(); j++){
                AgreeEntity agree = optionalList.get(j);
                if(agree.getAgreeID() == userAgreeListID){
                    userOptionalList.add(userAgreeListID);
                    break;
                }
            }
        }
        return userOptionalList;
    }

    // DB에 사용자가 선택한 약관 뭔지 저장하기
    public void saveUserAgreements(UserEntity savedUserEntity, List<Integer> userOptionalList) {
        if (userOptionalList.size() == 0) {
            return;
        }

        for (int i = 0; i < userOptionalList.size(); i++) {
            Integer agreeId = userOptionalList.get(i);
            UserAgreeEntity userAgreeEntity = new UserAgreeEntity();

            userAgreeEntity.setUser(savedUserEntity);

            AgreeEntity agreeEntity = agreeRepository.findById(agreeId).orElse(null);
            if (agreeEntity != null) {
                userAgreeEntity.setAgree(agreeEntity);
                userAgreeEntity.setAgreed(true);
                userAgreeRepository.save(userAgreeEntity);
            }
        }
    }

    public List<AgreeEntity> findAll() {
        return agreeRepository.findAll();
    }
    public void deleteById(int id) {
        agreeRepository.deleteById(id); // ID로 약관을 삭제
    }
    public AgreeEntity findById(int id) {
        Optional<AgreeEntity> agreement = agreeRepository.findById(id); // ID로 약관을 조회
        return agreement.orElse(null); // 약관이 있으면 반환, 없으면 null 반환
    }
    public AgreeEntity save(AgreeEntity agreement) {
        return agreeRepository.save(agreement); // 약관을 저장하거나 수정
    }
}
