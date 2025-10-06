package com.example.DOTORY.post.domain.entity;

import com.example.DOTORY.user.domain.entity.UserEntity;
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
public class Post extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long postId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userPK")    // 명시적으로 알 수 있게 하려고 외래키도 userPK로 작성함.
    private UserEntity user;

    private String title;
    private String content;

    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> imageUrls = new ArrayList<>();

    // 썸네일
    private String thumbnailUrl; // 첫 번째 이미지를 자동 세팅

    // 초반 개발 이미지 세팅시 사용한 엔티티
//    private String imageUrl;

    public void setImageUrls(List<String> imageUrls) {
        this.imageUrls = imageUrls;
        if(imageUrls != null && !imageUrls.isEmpty()) {
            this.thumbnailUrl = imageUrls.get(0); // 첫 번째 이미지를 썸네일로 지정
        }
    }

    // ===== 연관관계 매핑 =====
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Like> likes = new ArrayList<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Bookmark> bookmarks = new ArrayList<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();


}
