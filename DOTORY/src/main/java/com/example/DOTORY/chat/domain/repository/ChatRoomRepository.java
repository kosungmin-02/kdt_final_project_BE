package com.example.DOTORY.chat.domain.repository;

import com.example.DOTORY.chat.domain.entity.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    @Query("SELECT cr FROM ChatRoom cr JOIN cr.participants p1 JOIN cr.participants p2 " +
           "WHERE cr.roomType = 'ONE_ON_ONE' AND p1.user.id = :userId1 AND p2.user.id = :userId2")
    Optional<ChatRoom> findOneOnOneRoom(@Param("userId1") int userId1, @Param("userId2") int userId2);

}
