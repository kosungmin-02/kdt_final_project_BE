package com.example.DOTORY.user.domain.repository;

import com.example.DOTORY.user.domain.entity.AgreeEntity;
import com.example.DOTORY.user.domain.entity.AgreeType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AgreeRepository extends JpaRepository<AgreeEntity,Integer> {
    List<AgreeEntity> findByAgreeType(AgreeType agreeType);
}
