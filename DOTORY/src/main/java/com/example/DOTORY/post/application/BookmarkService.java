package com.example.DOTORY.post.application;

import com.example.DOTORY.post.domain.entity.Bookmark;
import com.example.DOTORY.post.domain.entity.Post;
import com.example.DOTORY.post.domain.repository.BookmarkRepository;
import com.example.DOTORY.post.domain.repository.PostRepository;
import com.example.DOTORY.user.domain.entity.UserEntity;
import com.example.DOTORY.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BookmarkService {

    private final BookmarkRepository bookmarkRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    @Transactional
    public String toggleBookmark(Long postId, int userPK) {

        // 게시글 조회
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글이 존재하지 않습니다."));


        // 사용자 조회하기
        UserEntity user = userRepository.findByUserPK(userPK)
                .orElseThrow(() -> new IllegalArgumentException("사용자가 존재핮 않습니다."));

        // 북마크 확인
        Optional<Bookmark> existing = bookmarkRepository.findByPost_PostIdAndUser_UserPK(postId, userPK);
        if (existing.isPresent()) {
            bookmarkRepository.delete(existing.get());
            return "북마크 취소";
        } else {
            Bookmark bookmark = Bookmark.builder()
                    .post(post)
                    .user(user)
                    .build();
            bookmarkRepository.save(bookmark);
            return "북마크 추가";
        }
    }

    public Long countBookmarks(Long postId) {
        return bookmarkRepository.countByPost_PostId(postId);
    }

    public Iterable<Long> getBookmarksByUser(int userPK) {
        // userPK로 북마크 목록 조회
        return bookmarkRepository.findByUser_UserPK(userPK)
                .stream()
                .map(bookmark -> bookmark.getPost().getPostId()) // postId만 반환
                .toList();
    }

}
