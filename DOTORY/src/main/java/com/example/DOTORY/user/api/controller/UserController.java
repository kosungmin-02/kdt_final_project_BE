package com.example.DOTORY.user.api.controller;

import com.example.DOTORY.global.code.dto.ApiResponse;
import com.example.DOTORY.user.api.dto.UserDTO;
import com.example.DOTORY.user.application.AgreeService;
import com.example.DOTORY.user.application.EmailSendService;
import com.example.DOTORY.user.application.PasswordService;
import com.example.DOTORY.user.application.UserService;
import com.example.DOTORY.global.jwt.JwtProvider;
import com.example.DOTORY.user.domain.entity.UserEntity;
import com.example.DOTORY.user.domain.repository.UserRepository;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@Slf4j
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final EmailSendService emailSendService;
    private final PasswordService passwordService;
    private final JwtProvider jwtProvider;
    private final UserRepository userRepository;
    private final AgreeService agreeService;

    // 회원가입
    @PostMapping("/registerConfirm")
    public ResponseEntity<ApiResponse<Void>> registerConfirm(@RequestBody UserDTO userDTO){
        log.info("UserController - registerConfirm 호출");
        int result = userService.registerConfirm(userDTO);

        if(result == 1){
            // 가입한 UserEntity 조회
            UserEntity savedUser = userRepository.findByUserID(userDTO.userID()).get();

            // 사용자가 동의한 약관 저장
            agreeService.saveUserAgreements(savedUser, userDTO.agree());
            return ResponseEntity.ok(ApiResponse.onSuccess(null));
        } else {
            return ResponseEntity.badRequest().body(ApiResponse.onFailure("REGISTER_FAIL", "회원가입 실패", null));
        }



    }

    // 이메일 인증 코드 전송
    @PostMapping("/emailVerification")
    public ResponseEntity<ApiResponse<Void>> sendEmailCode(@RequestParam("userEmail") String email, HttpSession session){
        String code = emailSendService.makeEmailCode();
        try {
            emailSendService.sendEmailForCode(email, code);
            session.setAttribute("CODE", code);
            session.setAttribute("EMAIL", email);
            session.setMaxInactiveInterval(5 * 60); // 5분

            return ResponseEntity.ok(ApiResponse.onSuccess(null));
        } catch (Exception e) {
            log.error("이메일 발송 실패", e);
            return ResponseEntity.status(500)
                    .body(ApiResponse.onFailure("EMAIL_SEND_FAIL", "이메일 발송 실패: " + e.getMessage(), null));
        }
    }

    // 이메일 인증코드 확인
    @PostMapping("/codeCheck")
    public ResponseEntity<ApiResponse<Void>> codeCheck(@RequestParam("userEmail") String email,
                                                       @RequestParam("inputCode") String code,
                                                       HttpSession session) {
        String sessionEmail = (String) session.getAttribute("EMAIL");
        String sessionCode = (String) session.getAttribute("CODE");

        if(sessionEmail != null && sessionCode != null &&
                sessionEmail.equals(email) && sessionCode.equals(code)) {
            return ResponseEntity.ok(ApiResponse.onSuccess(null));
        } else {
            return ResponseEntity.badRequest().body(ApiResponse.onFailure("CODE_MISMATCH", "인증번호 불일치", null));
        }
    }

    // 회원 가입 환영 이메일 전송
    @PostMapping("/sendWelcomeEmail")
    public ResponseEntity<ApiResponse<Void>> sendWelcomeEmail(@RequestBody UserDTO userDTO) throws MessagingException {
        emailSendService.sendEmailWelcome(userDTO);
        return ResponseEntity.ok(ApiResponse.onSuccess(null));
    }

    // 임시 비밀번호 발급
    @PostMapping("/findpassword")
    public ResponseEntity<ApiResponse<Void>> findpassword(@RequestBody UserDTO userDTO){
        log.info("UserController - findpassword()");
        int result = passwordService.findpassword(userDTO);

        if (result == 1) {
            return ResponseEntity.ok(ApiResponse.onSuccess(null));
        } else {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.onFailure("FIND_PASSWORD_FAIL", "아이디 또는 이메일이 일치하지 않습니다.", null));
        }
    }

    // 프로필 조회
    @GetMapping("/changeProfile")
    public ResponseEntity<ApiResponse<UserDTO>> getProfile(@RequestHeader("Authorization") String authHeader) {
        log.info("프론트에서 온 Authorization 헤더 = {}", authHeader);
        String token = authHeader.replace("Bearer ", "");
        String userID = jwtProvider.getUserIdFromToken(token);
        UserDTO profile = userService.profileChange(userID);

        return ResponseEntity.ok(ApiResponse.onSuccess(profile));
    }

    // 프로필 수정
    @PutMapping("/changeProfile")
    public ResponseEntity<ApiResponse<Void>> changeProfile(
            @RequestBody UserDTO userDTO,
            @RequestHeader("Authorization") String authHeader) {

        log.info("UserController - changeProfile(), userID = {}", userDTO.userID());

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(401).body(ApiResponse.onFailure("UNAUTHORIZED", "권한 없음", null));
        }

        String token = authHeader.substring(7);
        String userID = jwtProvider.getUserIdFromToken(token);

        if (!userID.equals(userDTO.userID())) {
            return ResponseEntity.status(401).body(ApiResponse.onFailure("UNAUTHORIZED", "권한 없음", null));
        }

        int result = userService.profileChangeConfirm(userDTO);
        if (result == 1) {
            return ResponseEntity.ok(ApiResponse.onSuccess(null));
        } else {
            return ResponseEntity.badRequest().body(ApiResponse.onFailure("PROFILE_UPDATE_FAIL", "프로필 수정 실패", null));
        }
    }
}
