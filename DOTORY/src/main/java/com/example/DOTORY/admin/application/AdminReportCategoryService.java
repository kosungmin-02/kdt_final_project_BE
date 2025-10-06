package com.example.DOTORY.admin.application;

import com.example.DOTORY.post.api.dto.request.ReportCategoryDTO;
import com.example.DOTORY.post.api.dto.response.ReportCategoryResponseDTO;
import com.example.DOTORY.post.domain.entity.ReportCategory;
import com.example.DOTORY.post.domain.repository.ReportCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminReportCategoryService {

    private final ReportCategoryRepository reportCategoryRepository;

    public List<ReportCategoryResponseDTO> getAllCategories() {
        return reportCategoryRepository.findAll()
                .stream()
                .map(rc -> new ReportCategoryResponseDTO(
                        rc.getCategoryId(),
                        rc.getCategoryName(),
                        rc.getReason()
                ))
                .collect(Collectors.toList());
    }

    @Transactional
    public ReportCategory createCategory(ReportCategoryDTO dto) {
        ReportCategory category = ReportCategory.builder()
                .categoryName(dto.categoryName())
                .reason(dto.reason())
                .build();

        return reportCategoryRepository.save(category);
    }

    @Transactional
    public ReportCategory updateCategory(Long id, ReportCategoryDTO dto) {
        ReportCategory category = reportCategoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 카테고리를 찾을 수 없습니다. id=" + id));

        category.setCategoryName(dto.categoryName());
        category.setReason(dto.reason());

        return reportCategoryRepository.save(category);
    }

    @Transactional
    public void deleteCategory(Long id) {
        reportCategoryRepository.deleteById(id);
    }
}
