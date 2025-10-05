package com.example.DOTORY.user.application;

import com.example.DOTORY.user.api.dto.UserDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.Random;

@Slf4j
@Service
public class EmailSendService {

    @Autowired
    JavaMailSender javaMailSender;

    // 이메일 인증 번호 생성
    public String makeEmailCode(){
        Random random = new Random();
        int code = 100000 + random.nextInt(900000);
        return String.valueOf(code);
    }

    // 인증 번호 이메일 발송 로직
    public void sendEmailForCode(String email, String code){
        log.info("EmailSendService - sendEmailForCode()");
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setTo(email);
        simpleMailMessage.setFrom("setilvereun@gmail.com");
        simpleMailMessage.setSubject("[도토리] 회원가입 인증 번호 입니다.");
        simpleMailMessage.setText("이메일 인증 번호 : " + code);

        try {
            javaMailSender.send(simpleMailMessage);
            log.info("이메일 발송 성공");
        } catch (Exception e) {
            log.error("이메일 발송 실패", e);
        }
    }

    // 가입 환영 이메일 전송
    public void sendEmailWelcome(UserDTO userDTO) {
        log.info("EmailSendService - sendEmailWelcome()");
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setTo(userDTO.userEmail());
        simpleMailMessage.setSubject("[도토리] 가입을 환영합니다.");
        simpleMailMessage.setText("도토리에 가입하신 것을 환영합니다! 앞으로 많은 소통을 해보아요!");
        simpleMailMessage.setFrom("setilvereun@gmail.com");

        javaMailSender.send(simpleMailMessage);

    }

    // 임시 비밀번호 이메일 발송 로직
    public void sendTempPasswordByEmail(String email, String tempPassword){
        log.info("UserService - sendTempPasswordByEmail()");

        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setTo(email);
        simpleMailMessage.setSubject("[도토리] 임시 비밀번호 안내입니다.");
        simpleMailMessage.setText("임시 비밀번호 : " + tempPassword);
        simpleMailMessage.setFrom("setilvereun@gmail.com");

        javaMailSender.send(simpleMailMessage);
    }
}
