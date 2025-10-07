package com.example.DOTORY.admin.api.dto;

import com.example.DOTORY.admin.domain.entity.MessageType;
import lombok.Data;

import java.util.List;

@Data
public class AdminMessageRequestDTO {
    private List<String> userIDs;
    private String messageTitle;
    private String messageContent;
    private MessageType messageType;
}
