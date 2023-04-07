package com.ronald.blogapptdd.controller;

import com.ronald.blogapptdd.dto.request.CreateCategoryRequest;
import com.ronald.blogapptdd.dto.request.UpdateCategoryRequest;
import com.ronald.blogapptdd.entity.Category;
import com.ronald.blogapptdd.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/category")
public class CategoryController {
    private final CategoryService categoryService;

    @Autowired
    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @PostMapping("/")
    public ResponseEntity<?> createCategory(@RequestBody CreateCategoryRequest request) {
        Category category = categoryService.createCategory(request);
        return ResponseEntity.ok(category);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getCategoryDetail(@PathVariable Long id) {
        Category category = categoryService.findCategoryById(id);
        return ResponseEntity.ok(category);
    }

    @GetMapping("/")
    public ResponseEntity<?> getAllCategory() {
        return ResponseEntity.ok(categoryService.findAllCategory());
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateCategory(@PathVariable Long id, @RequestBody UpdateCategoryRequest request) {
        Category category = categoryService.updateCategory(id, request);
        return ResponseEntity.ok(category);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.ok("Category deleted");
    }
}
