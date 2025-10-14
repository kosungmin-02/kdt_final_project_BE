package com.example.DOTORY.chat.domain.repository;

import com.example.DOTORY.chat.domain.entity.ChatParticipant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatParticipantRepository extends JpaRepository<ChatParticipant, Long> {

    List<ChatParticipant> findByUser_UserPK(int userPk);

}
