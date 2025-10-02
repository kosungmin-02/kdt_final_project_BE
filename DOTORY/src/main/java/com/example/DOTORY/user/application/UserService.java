package com.example.DOTORY.user.application;

import com.example.DOTORY.user.api.dto.UserDTO;
import com.example.DOTORY.user.domain.entity.UserEntity;
import com.example.DOTORY.user.domain.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Slf4j
public class UserService {
    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    EmailSendService emailSendService;

    // DB에 넣는게 성공했다면 1, 실패라면 0
    public final static int USER_REGISTER_SUCCESS = 1;
    public final static int USER_REGISTER_FAIL = 0;

    // 프로필 수정 후 DB에 넣는게 성공했다면 1, 실패라면 0
    public final static int USER_UPDATE_SUCCESS = 1;
    public final static int USER_UPDATE_FAIL = 0;



    // 의존성 주입 - 생성자 사용
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    // 회원가입 로직 처리
    public int registerConfirm(UserDTO userDTO){
        log.info("UserService - registerConfirm");

        // 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(userDTO.getUserPassword());
        userDTO.setUserPassword(encodedPassword);

        // UserDTO를 UserEntity로 변환
        UserEntity userEntity = UserEntity.builder()
                .userPK(userDTO.getUserPK())
                .userID(userDTO.getUserID())
                .userPassword(userDTO.getUserPassword())
                .userEmail(userDTO.getUserEmail())
                .userName(userDTO.getUserName())
                .userNickname(userDTO.getUserNickname())
                .build();

        // Repository에 저장하기
        UserEntity savedUserEntity = userRepository.save(userEntity);

        // 만약 savedUserEntity 저장하는데 아무런 예외 발생이 없다면 회원가입 성공.
        try{
            userRepository.save(userEntity);
            return USER_REGISTER_SUCCESS;
        }
        catch(Exception e){
            log.error("회원 가입 중 오류 발생", e);
            return USER_REGISTER_FAIL;
        }
    }


    // 회원 정보 수정.
    // (1) 회원 정보 가져오기
    public UserDTO profileChange(String loginedID){
        log.info("profileChange()");

        Optional<UserEntity> optionalUser = userRepository.findByUserID(loginedID);
        if(optionalUser.isPresent()){
            UserEntity userEntity = optionalUser.get();

            // UserEntity -> UserDTO
            UserDTO userDTO = UserDTO.builder()
                    .userPK(userEntity.getUserPK())
                    .userID(userEntity.getUserID())
                    .userEmail(userEntity.getUserEmail())
                    .userName(userEntity.getUserName())
                    .userNickname(userEntity.getUserNickname())
                    .userRole(userEntity.getUserRole())
                    .userCreatedDate(userEntity.getCreatedDate().toString())
                    .userUpdatedDate(userEntity.getUpdatedDate().toString())
                    .build();
            return userDTO;
        }
        return null;        // 조회했는데 없으면 그냥 nulll 반환.
    }


    // (2) 회원정보 수정 완료
    public int profileChangeConfirm(UserDTO userDTO){
        log.info("profileChangeConfirm()");

        String encodedPassword = passwordEncoder.encode(userDTO.getUserPassword());
        userDTO.setUserPassword(encodedPassword);

        // findByUserPK는 findByID와 동일한데, 편의상 명시적으로 findByUserPK로 함.
        Optional<UserEntity> optionalUser = userRepository.findByUserPK(userDTO.getUserPK());
        if(optionalUser.isPresent()){
            UserEntity userEntity = optionalUser.get();
            userEntity.setUserPassword(userDTO.getUserPassword());
            userEntity.setUserName(userDTO.getUserName());
            userEntity.setUserNickname(userDTO.getUserNickname());
            // 이메일은 본인인증용 / SNS 로그인 통합용으로 사용되기 때문에 수정 불가능하게 할 것.
            userEntity.setUpdatedDate(LocalDateTime.now()); // 수정한 날짜 입력


            // db에 업데이트
            userRepository.save(userEntity);
            return USER_UPDATE_SUCCESS;
        }

        return USER_UPDATE_FAIL;

    }
}
