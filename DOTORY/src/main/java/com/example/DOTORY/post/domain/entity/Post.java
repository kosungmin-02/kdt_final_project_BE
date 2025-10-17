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
    @JoinColumn(name = "userPK")
    private UserEntity user;

    private String caption;

    @ElementCollection(fetch = FetchType.EAGER)
    @Builder.Default
    private List<String> imageUrls = new ArrayList<>();

    private String thumbnailUrl;

    public void setImageUrls(List<String> imageUrls) {
        this.imageUrls = imageUrls;
        if(imageUrls != null && !imageUrls.isEmpty()) {
            this.thumbnailUrl = imageUrls.get(0);
        }
    }

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Like> likes = new ArrayList<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Bookmark> bookmarks = new ArrayList<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Comment> comments = new ArrayList<>();

    @OneToOne(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Decoration decoration;

}