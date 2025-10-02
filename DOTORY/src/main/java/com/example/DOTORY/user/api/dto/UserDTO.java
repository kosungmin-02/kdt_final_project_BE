package com.example.DOTORY.user.api.dto;

import com.example.DOTORY.user.domain.entity.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
// 사용자 아이디, 비밀번호, 이메일, 이름, 닉네임
public class UserDTO implements Serializable {

    // DTO를 세션에 저장할 것이므로 Serial 이용
    private static final long serialVersionUID = 1L;

    private int userPK;
    private String userID;
    private String userPassword;
    private String userName;
    private String userNickname;
    private String userEmail;
    private UserRole userRole;
    private String userCreatedDate;
    private String userUpdatedDate;

}
