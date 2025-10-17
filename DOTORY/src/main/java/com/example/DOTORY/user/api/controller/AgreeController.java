package com.example.DOTORY.user.api.controller;


import com.example.DOTORY.user.api.dto.AgreeDTO;
import com.example.DOTORY.user.application.AgreeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:5173")
@Tag(name = "Agree API", description = "약관 관련 API (필수약관/선택약관)")
@RestController
@RequestMapping("/api/users")
@Slf4j
@RequiredArgsConstructor
public class AgreeController {

    private final AgreeService agreeService;

    // 필수약관 / 선택약관 조회
    @Operation(summary = "약관 조회하기", description = "필수/선택 약관 뭐뭐있는지 조회 가능.")
    @GetMapping("/agreements")
    public AgreementsResponse getAgreements() {
        log.info("AgreeRestController - getAgreements()");
        List<AgreeDTO> requiredList = agreeService.requiredAgree();
        List<AgreeDTO> optionalList = agreeService.optionalAgree();

        return new AgreementsResponse(requiredList, optionalList);
    }

    // 사용자가 선택한 약관 저장
    @Operation(summary = "사용자의 선택 약관 저장", description = "사용자가 어떤 선택 약관을 선택했는지를 저장한다.")
    @PostMapping("/agreements")
    public SaveAgreementsResponse saveAgreements(@RequestBody UserAgreementsRequest request) {
        log.info("AgreeRestController - saveAgreements(): {}", request);

        List<Integer> userOptionalList = agreeService.saveOnlyOptional(request.getAgreements());

        return new SaveAgreementsResponse(userOptionalList);
    }


    public static class UserAgreementsRequest {
        private List<Integer> agreements;

        public List<Integer> getAgreements() {
            return agreements;
        }

        public void setAgreements(List<Integer> agreements) {
            this.agreements = agreements;
        }
    }

    public static class AgreementsResponse {
        private List<AgreeDTO> requiredList;
        private List<AgreeDTO> optionalList;

        public AgreementsResponse(List<AgreeDTO> requiredList, List<AgreeDTO> optionalList) {
            this.requiredList = requiredList;
            this.optionalList = optionalList;
        }

        public List<AgreeDTO> getRequiredList() { return requiredList; }
        public List<AgreeDTO> getOptionalList() { return optionalList; }
    }

    public static class SaveAgreementsResponse {
        private List<Integer> userOptionalList;

        public SaveAgreementsResponse(List<Integer> userOptionalList) {
            this.userOptionalList = userOptionalList;
        }

        public List<Integer> getUserOptionalList() { return userOptionalList; }
    }
}
