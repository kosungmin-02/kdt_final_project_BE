// StickerRepository.java
package com.example.DOTORY.user.domain.repository;

import com.example.DOTORY.user.domain.entity.StickerEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StickerRepository extends JpaRepository<StickerEntity, Long> {
    void deleteByDecorationId(Long decorationId);
}