package com.example.DOTORY.user.domain.repository;

import com.example.DOTORY.user.domain.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity,Integer> {


    // 로그인 - 회원 ID로 회원이 있는지 조희하기
    public Optional<UserEntity> findByUserID(String userID);

    // findByID와 동일하다. 명시적으로 표현하기 위해 findByUserPK로 만듦.
    public Optional<UserEntity> findByUserPK(int userPK);

    // 유저ID와 이메일로 유저 정보 조회
    public Optional<UserEntity> findByUserIDAndUserEmail(String userID, String userEmail);

    // 로그아웃시에도 사용.
    Optional<UserEntity> findByUserEmail(String email);

    Optional<UserEntity> findByUserNickname(String userNickname);

    List<UserEntity> findByUserIDContainingIgnoreCase(String keyword);

    List<UserEntity> findAllByUserIDIn(List<String> userIDs);

    Integer userPK(int userPK);
}