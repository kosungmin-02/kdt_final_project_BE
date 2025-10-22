package com.example.DOTORY.post.application;

import com.example.DOTORY.post.api.dto.request.PostSearchRequest;
import com.example.DOTORY.post.api.dto.response.PostListResponse;
import com.example.DOTORY.post.domain.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostSearchService {

    private final PostRepository postRepository;

     // 키워드를 포함하는 해시태그 게시글 목록을 정렬하여 조회
    public List<PostListResponse> searchPostsByKeyword(PostSearchRequest request) {
        // PostRepository를 통해 DB에서 게시글 엔티티 목록을 가져옴
        // 정렬 기준을 Service 계층에서 결정하여 Repository로 전달

        String sortCriteria = request.sort().toUpperCase();

        List<PostListResponse> posts = switch (sortCriteria) {
            case "LATEST" -> postRepository.findByHashtagKeywordOrderByLatest(request.keyword());
            case "POPULAR" -> postRepository.findByHashtagKeywordOrderByPopular(request.keyword());
            default -> throw new IllegalArgumentException("유효하지 않은 정렬 기준입니다: " + sortCriteria);
            // ErrorStatus를 이용한 예외 처리로 변경 필요
        };

        // 현재는 Repository가 DTO를 반환하도록 가정

        return posts;
    }
}
