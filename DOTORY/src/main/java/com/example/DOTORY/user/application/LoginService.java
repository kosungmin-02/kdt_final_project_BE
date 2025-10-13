package com.example.DOTORY.user.application;

import com.example.DOTORY.user.api.dto.UserDTO;
import com.example.DOTORY.user.domain.entity.UserLogin;
import com.example.DOTORY.user.domain.repository.UserRepository;
import com.example.DOTORY.global.jwt.JwtProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class LoginService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    JwtProvider jwtProvider;

    public String loginConfirm(UserDTO userDTO){
        log.info("LoginService - loginConfirm");

        return userRepository.findByUserID(userDTO.userID())
                .filter(u -> passwordEncoder.matches(userDTO.userPassword(), u.getUserPassword()))
                .map(u -> {
                    u.setUserLogin(UserLogin.LOGIN);
                    userRepository.save(u);
                    return jwtProvider.generateToken(u.getUserID());
                })
                .orElse(null);
    }
}
