package com.example.DOTORY.user.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
@Entity
@Table(name="AGREE")
public class AgreeEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="AGREEID")
    private int agreeID;

    @Column(name="AGREETITLE")
    private String agreeTitle;

    @Column(name="AGREECONTENT", columnDefinition = "LONGTEXT")
    private String agreeContent;

    @Enumerated(EnumType.STRING)    // 약관동의의 타입은 enum
    private AgreeType agreeType;
}
