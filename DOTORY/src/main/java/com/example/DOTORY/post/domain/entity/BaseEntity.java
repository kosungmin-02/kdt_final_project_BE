package com.example.DOTORY.post.domain.entity;

import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@MappedSuperclass
public class BaseEntity {

    private LocalDateTime createdDate;

    private LocalDateTime updatedDate;


}
