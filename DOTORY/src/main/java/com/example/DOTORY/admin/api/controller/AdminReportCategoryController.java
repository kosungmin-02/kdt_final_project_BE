package com.example.DOTORY.admin.api.controller;

import com.example.DOTORY.admin.application.AdminReportCategoryService;
import com.example.DOTORY.post.api.dto.request.ReportCategoryDTO;
import com.example.DOTORY.post.api.dto.response.ReportCategoryResponseDTO;
import com.example.DOTORY.post.domain.entity.ReportCategory;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "관리자 - 신고 카테고리")
@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/admin/report-category")
@RequiredArgsConstructor
public class AdminReportCategoryController {

    private final AdminReportCategoryService adminReportCategoryService;

    @Operation(summary = "모든 신고 카테고리 조회")
    @GetMapping
    public List<ReportCategoryResponseDTO> getAllCategories() {
        return adminReportCategoryService.getAllCategories();
    }

    @Operation(summary = "신고 카테고리 생성")
    @PostMapping
    public ReportCategory createCategory(@RequestBody ReportCategoryDTO dto) {
        return adminReportCategoryService.createCategory(dto);
    }

    @Operation(summary = "신고 카테고리 수정")
    @PutMapping("/{id}")
    public ReportCategory updateCategory(@PathVariable Long id, @RequestBody ReportCategoryDTO dto) {
        return adminReportCategoryService.updateCategory(id, dto);
    }

    @Operation(summary = "신고 카테고리 삭제")
    @DeleteMapping("/{id}")
    public void deleteCategory(@PathVariable Long id) {
        adminReportCategoryService.deleteCategory(id);
    }
}
