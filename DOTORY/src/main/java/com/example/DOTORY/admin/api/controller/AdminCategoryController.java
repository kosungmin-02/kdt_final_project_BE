package com.example.DOTORY.admin.api.controller;

import com.example.DOTORY.admin.application.AdminPostCategoryService;
import com.example.DOTORY.post.api.dto.request.PostCategoryDTO;
import com.example.DOTORY.post.api.dto.response.PostCategoryResponseDTO;
import com.example.DOTORY.post.domain.entity.PostCategory;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/admin/category")
@RequiredArgsConstructor
@Tag(name = "Admin Category", description = "관리자용 카테고리 관리 API")
public class AdminCategoryController {

    private final AdminPostCategoryService adminPostCategoryService;

    @Operation(summary = "전체 카테고리 조회", description = "모든 카테고리를 조회합니다.")
    @GetMapping
    public List<PostCategoryResponseDTO> getAllCategories() {
        return adminPostCategoryService.getAllCategories();
    }

    @Operation(summary = "카테고리 생성", description = "새로운 카테고리를 생성합니다.")
    @PostMapping
    public PostCategory createCategory(
            @Parameter(description = "생성할 카테고리 정보") @RequestBody PostCategoryDTO dto) {
        return adminPostCategoryService.createCategory(dto);
    }

    @Operation(summary = "카테고리 수정", description = "카테고리 ID에 해당하는 카테고리를 수정합니다.")
    @PutMapping("/{id}")
    public PostCategory updateCategory(
            @Parameter(description = "수정할 카테고리 ID") @PathVariable Long id,
            @Parameter(description = "수정할 카테고리 정보") @RequestBody PostCategoryDTO dto) {
        return adminPostCategoryService.updateCategory(id, dto);
    }

    @Operation(summary = "카테고리 삭제", description = "카테고리 ID에 해당하는 카테고리를 삭제합니다.")
    @DeleteMapping("/{id}")
    public void deleteCategory(
            @Parameter(description = "삭제할 카테고리 ID") @PathVariable Long id) {
        adminPostCategoryService.deleteCategory(id);
    }
}
