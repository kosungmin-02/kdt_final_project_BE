package com.example.DOTORY.admin.application;

import com.example.DOTORY.global.code.status.ErrorStatus;
import com.example.DOTORY.global.exception.GeneralException;
import com.example.DOTORY.post.api.dto.request.PostCategoryDTO;
import com.example.DOTORY.post.api.dto.response.PostCategoryResponseDTO;
import com.example.DOTORY.post.domain.entity.PostCategory;
import com.example.DOTORY.post.domain.repository.PostCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminPostCategoryService {

    private final PostCategoryRepository postCategoryRepository;

    public List<PostCategoryResponseDTO> getAllCategories() {
        return postCategoryRepository.findAll()
                .stream()
                .map(cat -> new PostCategoryResponseDTO(
                        cat.getCategoryId(),
                        cat.getCategoryName(),
                        cat.getParent() != null ? cat.getParent().getCategoryId() : null,
                        cat.getSortOrder()
                ))
                .toList();
    }

    @Transactional
    public PostCategory createCategory(PostCategoryDTO dto) {
        PostCategory parent = null;

        if (dto.parentId() != null) {
            parent = postCategoryRepository.findById(dto.parentId())
                    .orElseThrow(() -> new GeneralException(ErrorStatus.RESOURCE_NOT_FOUND, "부모 카테고리가 존재하지 않습니다."));
        }

        PostCategory category = PostCategory.builder()
                .categoryName(dto.categoryName())
                .sortOrder(dto.sortOrder())
                .parent(parent)
                .build();

        return postCategoryRepository.save(category);
    }

    @Transactional
    public PostCategory updateCategory(Long id, PostCategoryDTO dto) {
        PostCategory category = postCategoryRepository.findById(id)
                .orElseThrow(() -> new GeneralException(ErrorStatus.RESOURCE_NOT_FOUND, "존재하지 않는 카테고리입니다."));

        PostCategory parent = null;
        if (dto.parentId() != null) {
            parent = postCategoryRepository.findById(dto.parentId())
                    .orElseThrow(() -> new GeneralException(ErrorStatus.RESOURCE_NOT_FOUND, "상위 카테고리가 존재하지 않습니다."));
        }

        category.setCategoryName(dto.categoryName());
        category.setParent(parent);
        category.setSortOrder(dto.sortOrder());

        return postCategoryRepository.save(category);
    }

    @Transactional
    public void deleteCategory(Long categoryId) {
        List<PostCategory> children = postCategoryRepository.findAllByParent_CategoryId(categoryId);
        for (PostCategory child : children) {
            deleteCategory(child.getCategoryId());
        }

        postCategoryRepository.deleteById(categoryId);
    }
}