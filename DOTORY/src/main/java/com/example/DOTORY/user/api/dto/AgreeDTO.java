package com.example.DOTORY.user.api.dto;

import com.example.DOTORY.user.domain.entity.AgreeEntity;
import com.example.DOTORY.user.domain.entity.AgreeType;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AgreeDTO {
    private int agreeId;
    private String agreeTitle;
    private String agreeContent;
    private AgreeType agreeType;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;


    // DTO -> Entity 변환하기
    public AgreeEntity AgreeDTOtoAgreeEntity(){
        AgreeEntity agreeEntity = new AgreeEntity();
        agreeEntity.setAgreeTitle(agreeTitle);
        agreeEntity.setAgreeContent(agreeContent);
        agreeEntity.setAgreeType(agreeType);
        return agreeEntity;
    }

    // Entity -> DTO 변환하기
    public AgreeDTO (AgreeEntity agreeEntity){
        this.agreeId = agreeEntity.getAgreeID();
        this.agreeTitle = agreeEntity.getAgreeTitle();
        this.agreeContent = agreeEntity.getAgreeContent();
        this.agreeType = agreeEntity.getAgreeType();
        this.createdDate = agreeEntity.getCreatedDate();
        this.updatedDate = agreeEntity.getUpdatedDate();
    }
}
