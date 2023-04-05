package com.ronald.blogapptdd.service;

import com.ronald.blogapptdd.dto.request.CreateCategoryRequest;
import com.ronald.blogapptdd.dto.request.UpdateCategoryRequest;
import com.ronald.blogapptdd.entity.Category;

import java.util.List;

public interface CategoryService {
    Category createCategory(CreateCategoryRequest request);

    Category findCategoryById(Long id);

    List<Category> findAllCategory();

    Category updateCategory(Long categoryId, UpdateCategoryRequest request);

    void deleteCategory(Long id);
}
