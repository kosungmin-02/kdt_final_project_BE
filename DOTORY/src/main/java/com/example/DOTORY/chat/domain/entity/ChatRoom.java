package com.example.DOTORY.chat.domain.entity;

import com.example.DOTORY.post.domain.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatRoom extends BaseEntity { 

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = true) // 1:1 채팅은 이름이 없을 수 있음
    private String roomName;

    @Column(nullable = true)
    private String roomImage;

    @Column(nullable = true)
    private String description;

    @OneToMany(mappedBy = "chatRoom", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<ChatParticipant> participants = new ArrayList<>();
}
