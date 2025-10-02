package com.example.DOTORY.user.api.controller;


import com.example.DOTORY.user.api.dto.AgreeDTO;
import com.example.DOTORY.user.application.AgreeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@Slf4j
@RequiredArgsConstructor
public class AgreeController {

    private final AgreeService agreeService;

    // 필수약관 / 선택약관 조회
    @GetMapping("/agreements")
    public AgreementsResponse getAgreements() {
        log.info("AgreeRestController - getAgreements()");
        List<AgreeDTO> requiredList = agreeService.requiredAgree();
        List<AgreeDTO> optionalList = agreeService.optionalAgree();

        return new AgreementsResponse(requiredList, optionalList);
    }

    // 사용자가 선택한 약관 저장
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
