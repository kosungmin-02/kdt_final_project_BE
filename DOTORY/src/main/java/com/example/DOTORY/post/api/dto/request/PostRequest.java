package com.example.DOTORY.post.api.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PostRequest {

    private String caption;
    private List<String> imageUrls;

    // Decoration 관련
    private String frameColor;       // ex: #ffffff
    private String framePattern;     // none | dots | stripe | grid | paper
    private String patternColor;     // ex: #000000
    private Double patternOpacity;   // 0~1
    private Integer rotation;        // 0, 90, 180, 270
    private List<String> keywords;         // CSV 형태로 저장.


}
