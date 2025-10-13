package com.example.DOTORY.notification.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification {

    private String id;
    private String message;
    private String relatedUrl;
    private boolean isRead;
    private long createdAt;
}
