package com.example.DOTORY.global.post.api.dto.response;

import com.example.DOTORY.global.post.domain.ReportReason;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SimpleResponse {

    private String message;

    private Long reportId;
    private Long postId;
    private Long userId;
    private ReportReason reason;
    private String description;
    private LocalDateTime createdDate;

}
