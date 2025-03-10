package org.orderhub.pr.category.controller;

import lombok.RequiredArgsConstructor;
import org.orderhub.pr.category.dto.request.CategoryRegisterRequest;
import org.orderhub.pr.category.dto.request.CategoryUpdateRequest;
import org.orderhub.pr.category.dto.response.CategoryRegisterResponse;
import org.orderhub.pr.category.dto.response.CategoryTreeResponse;
import org.orderhub.pr.category.dto.response.CategoryUpdateResponse;
import org.orderhub.pr.category.service.CategoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    public ResponseEntity<List<CategoryTreeResponse>> getAllCategories() {
        List<CategoryTreeResponse> categoryTree = categoryService.getAllCategories();
        return ResponseEntity.ok(categoryTree);
    }

    @GetMapping("/active")
    public ResponseEntity<List<CategoryTreeResponse>> getAllActiveCategories() {
        List<CategoryTreeResponse> activeCategories = categoryService.getAllCategoriesByActive();
        return ResponseEntity.ok(activeCategories);
    }

    @PostMapping
    public ResponseEntity<CategoryRegisterResponse> registerCategory(
            @RequestBody CategoryRegisterRequest request
    ) {
        CategoryRegisterResponse response = categoryService.categoryRegister(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryTreeResponse> getCategoryById(@PathVariable Long id) {
        return ResponseEntity.ok(new CategoryTreeResponse(categoryService.findById(id)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoryUpdateResponse> updateCategory(
            @PathVariable Long id,
            @RequestBody CategoryUpdateRequest request
    ) {
        CategoryUpdateResponse response = categoryService.categoryUpdate(request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        categoryService.categoryDelete(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/restore")
    public ResponseEntity<Void> restoreCategory(@PathVariable Long id) {
        categoryService.categoryRestore(id);
        return ResponseEntity.noContent().build();
    }

}
