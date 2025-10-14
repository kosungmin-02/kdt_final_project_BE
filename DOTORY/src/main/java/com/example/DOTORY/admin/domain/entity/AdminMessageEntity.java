package com.example.DOTORY.admin.domain.entity;

import com.example.DOTORY.post.domain.entity.BaseEntity;
import com.example.DOTORY.user.domain.entity.UserEntity;
import jakarta.persistence.*;
import lombok.*;


@Entity
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminMessageEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private int adminMessageID; // 고유번호

    // 특정 사용자에게 메세지 보내기용
    @ManyToOne
    @JoinColumn(name="USERPK")
    private UserEntity user;

    private String messageTitle;
    private String messageContent;
    private MessageType messageType;  // 메세지 타입 - 경고 / 공지 / ....
    @Builder.Default
    private boolean messageRead = false; // 사용자가 읽었는지 아닌지 여부
}

