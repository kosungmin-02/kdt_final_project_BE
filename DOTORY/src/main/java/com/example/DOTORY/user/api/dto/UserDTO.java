package com.example.DOTORY.user.api.dto;

import com.example.DOTORY.user.domain.entity.UserRole;
import java.io.Serializable;

public record UserDTO(
        int userPK,
        String userID,
        String userPassword,
        String userName,
        String userNickname,
        String userEmail,
        UserRole userRole,
        String userCreatedDate,
        String userUpdatedDate
) implements Serializable {

    private static final long serialVersionUID = 1L;

}
