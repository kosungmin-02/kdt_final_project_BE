package com.example.DOTORY.global.post.application;

import com.example.DOTORY.global.post.domain.entity.Bookmark;
import com.example.DOTORY.global.post.domain.entity.Post;
import com.example.DOTORY.global.post.domain.repository.BookmarkRepository;
import com.example.DOTORY.global.post.domain.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BookmarkService {

    private final BookmarkRepository bookmarkRepository;
    private final PostRepository postRepository;

    @Transactional
    public String toggleBookmark(Long postId, Long userId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글이 존재하지 않습니다."));

        Optional<Bookmark> existing = bookmarkRepository.findByPost_PostIdAndUserId(postId, userId);
        if (existing.isPresent()) {
            bookmarkRepository.delete(existing.get());
            return "북마크 취소";
        } else {
            Bookmark bookmark = Bookmark.builder()
                    .post(post)
                    .userId(userId)
                    .build();
            bookmarkRepository.save(bookmark);
            return "북마크 추가";
        }
    }

    public Long countBookmarks(Long postId) {
        return bookmarkRepository.countByPost_PostId(postId);
    }

}
