package com.example.DOTORY.user.api.controller;

import com.example.DOTORY.global.code.dto.ApiResponse;
import com.example.DOTORY.global.code.status.ErrorStatus;
import com.example.DOTORY.global.exception.GeneralException;
import com.example.DOTORY.user.api.dto.UserDTO;
import com.example.DOTORY.user.application.AgreeService;
import com.example.DOTORY.user.application.EmailSendService;
import com.example.DOTORY.user.application.PasswordService;
import com.example.DOTORY.user.application.UserService;
import com.example.DOTORY.global.jwt.JwtProvider;
import com.example.DOTORY.user.domain.entity.UserEntity;
import com.example.DOTORY.user.domain.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@Slf4j
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final EmailSendService emailSendService;
    private final PasswordService passwordService;
    private final JwtProvider jwtProvider;
    private final AgreeService agreeService;
    private final UserRepository userRepository;

    @Operation(summary = "일반 회원 가입 용도", description = "일반 회원가입 용도로 약관동의 -> 회원정보 입력 등을 거쳐서 회원가입.")
    @PostMapping("/registerConfirm")
    public ResponseEntity<ApiResponse<Void>> registerConfirm(@RequestBody UserDTO userDTO){
        log.info("UserController - registerConfirm 호출");
        UserEntity savedUser = userService.registerConfirm(userDTO);
        agreeService.saveUserAgreements(savedUser, userDTO.agree());
        return ResponseEntity.ok(ApiResponse.onSuccess(null));
    }

    @Operation(summary = "이메일 인증 코드 전송", description = "이메일을 통해서 본인인증을 하고자 회원가입시 작성한 이메일로 인증 코드를 보냄.")
    @PostMapping("/emailVerification")
    public ResponseEntity<ApiResponse<Void>> sendEmailCode(@RequestParam("userEmail") String email, HttpSession session){
        userService.checkEmailDuplicate(email);

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

    @Operation(summary = "이메일 인증 코드 확인", description = "회원가입시 이메일로 보낸 인증 코드를 맞게 작성했는지 확인함.")
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

    @Operation(summary = "가입 환영 메일 전송", description = "회원 가입 완료시 가입환영 메일을 보내줌.")
    @PostMapping("/sendWelcomeEmail")
    public ResponseEntity<ApiResponse<Void>> sendWelcomeEmail(@RequestBody UserDTO userDTO) throws MessagingException {
        emailSendService.sendEmailWelcome(userDTO);
        return ResponseEntity.ok(ApiResponse.onSuccess(null));
    }

    @Operation(summary = "임시 비밀번호 발급", description = "비밀번호를 잊어버렸을 때 아이디, 이메일을 입력하면 해당 이메일로 임시 비밀번호를 보내줌.")
    @PostMapping("/findpassword")
    public ResponseEntity<ApiResponse<Void>> findpassword(@RequestBody UserDTO userDTO){
        log.info("UserController - findpassword()");
        passwordService.findPassword(userDTO);
        return ResponseEntity.ok(ApiResponse.onSuccess(null));
    }

    @Operation(summary = "프로필 조회", description = "프로필 즉 회원 기본 정보를 조회할 수 있음.")
    @GetMapping("/changeProfile")
    public ResponseEntity<ApiResponse<UserDTO>> getProfile(@RequestHeader("Authorization") String authHeader) {
        log.info("프론트에서 온 Authorization 헤더 = {}", authHeader);
        String token = authHeader.replace("Bearer ", "");
        String userID = jwtProvider.getUserIdFromToken(token);
        UserDTO profile = userService.profileChange(userID);

        return ResponseEntity.ok(ApiResponse.onSuccess(profile));
    }

    @Operation(summary = "프로필 수정", description = "로그인한 회원은 자신의 프로필 수정")
    @PutMapping(value = "/changeProfile", consumes = { "multipart/form-data" })
    public ResponseEntity<ApiResponse<UserDTO>> changeProfile(
            @RequestPart("user") UserDTO userDTO,
            @RequestPart(value = "userAvatar", required = false) MultipartFile userAvatar,
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

        // 프로필 정보 업데이트
        userService.profileChangeConfirm(userDTO, userAvatar);

        return ResponseEntity.ok(ApiResponse.onSuccess(null));
    }

    @PostMapping("/mypage/passwordChangeConfirm")
    public ResponseEntity<?> changePassword(
            @RequestBody Map<String, String> req,
            @RequestHeader("Authorization") String authHeader
    ) {
        String token = authHeader.replace("Bearer ", "");
        String loginUserID = jwtProvider.getUserIdFromToken(token);

        String userID = req.get("userID");
        String currentPassword = req.get("currentPassword");
        String newPassword = req.get("newPassword");

        if(!loginUserID.equals(userID)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("권한 없음");
        }

        try {
            userService.changePassword(userID, currentPassword, newPassword);
            return ResponseEntity.ok("비밀번호가 변경되었습니다.");
        } catch (GeneralException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        }
    }



    @GetMapping("/avatar/{userID}")
    public ResponseEntity<Resource> getAvatar(@PathVariable String userID) {
        UserEntity user = userRepository.findByUserID(userID)
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));

        if(user.getUserAvatar() == null) {
            throw new GeneralException(ErrorStatus.USER_NOT_FOUND, "아바타가 없습니다.");
        }

        try {
            // 실제 서버 경로
            Path path = Paths.get("avatars/" + user.getUserAvatar().substring("/avatars/".length()));
            Resource resource = new UrlResource(path.toUri());

            if(!resource.exists()) {
                throw new GeneralException(ErrorStatus.FILE_NOT_FOUND, "아바타 파일을 찾을 수 없습니다.");
            }

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_TYPE, "image/png") // PNG이면 image/png
                    .body(resource);

        } catch (Exception e) {
            throw new GeneralException(ErrorStatus.FILE_NOT_FOUND, "아바타 로드 실패");
        }
    }

}