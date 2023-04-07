package com.ronald.blogapptdd.service.impl;

import com.ronald.blogapptdd.dto.request.CreateCategoryRequest;
import com.ronald.blogapptdd.dto.request.UpdateCategoryRequest;
import com.ronald.blogapptdd.entity.Category;
import com.ronald.blogapptdd.exception.CategoryNotFoundException;
import com.ronald.blogapptdd.repository.CategoryRepository;
import com.ronald.blogapptdd.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public Category createCategory(CreateCategoryRequest request) {
        Category category = Category.builder()
                .description(request.getDescription())
                .name(request.getName())
                .build();
        return categoryRepository.save(category);
    }

    @Override
    public Category findCategoryById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category Not Found"));
    }

    @Override
    public List<Category> findAllCategory() {
        return categoryRepository.findAll();
    }

    @Override
    public Category updateCategory(Long categoryId, UpdateCategoryRequest request) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new CategoryNotFoundException("Category not found"));

        Category updatedCategory = compareAndUpdate(category, request);

        return categoryRepository.save(updatedCategory);
    }

    @Override
    public void deleteCategory(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException("Category not found"));
        categoryRepository.deleteById(category.getId());
    }

    private Category compareAndUpdate(Category category, UpdateCategoryRequest request) {

        if (!category.getName().equals(request.getName())) {
            category.setName(request.getName());
        }

        if (!category.getDescription().equals(request.getDescription())) {
            category.setDescription(request.getDescription());
        }

        return category;
    }
}
