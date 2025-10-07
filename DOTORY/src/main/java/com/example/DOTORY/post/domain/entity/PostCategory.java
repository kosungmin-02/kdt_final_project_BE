package com.example.DOTORY.post.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long categoryId;

    private String categoryName;

    private Integer sortOrder;

    // 자기참조 연관관계
    @ManyToOne
    @JoinColumn(name = "parent_id")
    private PostCategory parent;

}
