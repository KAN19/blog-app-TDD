package com.ronald.blogapptdd.service.impl;

import com.ronald.blogapptdd.dto.request.CreateCategoryRequest;
import com.ronald.blogapptdd.dto.request.UpdateCategoryRequest;
import com.ronald.blogapptdd.entity.Category;
import com.ronald.blogapptdd.exception.CategoryNotFoundException;
import com.ronald.blogapptdd.repository.CategoryRepository;
import com.ronald.blogapptdd.service.CategoryService;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.sql.SQLException;
import java.util.Optional;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.assertj.core.api.Java6Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@TestPropertySource("/application-test.properties")
@ExtendWith(MockitoExtension.class)
class CategoryServiceImplTest {

    @Mock
    private CategoryRepository categoryRepository;

    private CategoryService underTest;

    @BeforeEach
    void setUp() {
        underTest = new CategoryServiceImpl(categoryRepository);
    }

    @AfterEach
    void tearDown() {
        categoryRepository.deleteAll();
    }

    @Test
    @DisplayName("Create category with valid request")
    void createCategory() {
        //given
        CreateCategoryRequest request = CreateCategoryRequest.builder()
                .name("Java")
                .description("Java ne")
                .build();
        Category category = Category.builder()
                .description(request.getDescription())
                .name(request.getName())
                .build();

        // when
        underTest.createCategory(request);

        // then
        ArgumentCaptor<Category> categoryArgumentCaptor = ArgumentCaptor.forClass(Category.class);

        verify(categoryRepository).save(categoryArgumentCaptor.capture());

        Category capturedCategory = categoryArgumentCaptor.getValue();
        assertThat(capturedCategory)
                .isEqualToComparingOnlyGivenFields(category, "name", "description");

    }

    @Test
    @DisplayName("Get category detail by Id")
    void getCategoryDetail() {
        Category category = Category.builder()
                .id(1L)
                .name("Java")
                .description("Java ne")
                .build();
        given(categoryRepository.findById(1L)).willReturn(Optional.ofNullable(category));

        //when
        underTest.findCategoryById(1L);

        //then
        verify(categoryRepository).findById(1L);
    }

    @Test
    @DisplayName("Get category detail with invalid Id ")
    void getCategoryDetailThrowException() {
        //given
        given(categoryRepository.findById(1L)).willReturn(Optional.empty());

        //when
        //then
        assertThatThrownBy(() -> underTest.findCategoryById(any())).isInstanceOf(RuntimeException.class);
    }

    @Test
    @DisplayName("Get all categories")
    void getAllCategory() {
        //given

        //when
        underTest.findAllCategory();

        //then
        verify(categoryRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Update category")
    void canUpdateCategory() {
        //given
        Category oldCategory = Category.builder()
                .name("Javascript")
                .description("Javascript is good!")
                .build();
        Long categoryId = 1L;

        UpdateCategoryRequest request = UpdateCategoryRequest.builder()
                .name("Java")
                .description("Java ne")
                .build();
        Category updatedCategory = Category.builder()
                .name(request.getName())
                .description(request.getDescription())
                .build();

        given(categoryRepository.findById(1L)).willReturn(Optional.of(oldCategory));
        given(categoryRepository.save(any(Category.class))).willReturn(updatedCategory);

        //when
        Category category = underTest.updateCategory(categoryId, request);

        //then
        assertThat(updatedCategory).isEqualTo(category);
        verify(categoryRepository).findById(any());
        verify(categoryRepository).save(any(Category.class));
    }

    @Test
    @DisplayName("Update category with invalid Id")
    void canNotUpdateCategory() {
        //given
        given(categoryRepository.findById(any())).willReturn(Optional.empty());

        //when
        UpdateCategoryRequest request = UpdateCategoryRequest.builder()
                .name("Java")
                .description("Java ne")
                .build();
        Long categoryId = 1L;

        //then
        assertThatThrownBy(() -> underTest.updateCategory(categoryId, request))
                .isInstanceOf(CategoryNotFoundException.class);
    }

    @Test
    @DisplayName("Delete category")
    void canDeleteCategory() {
        //given
        Category category = Category.builder()
                .id(1L)
                .name("Java")
                .description("Java ne")
                .build();
        given(categoryRepository.findById(1L)).willReturn(Optional.of(category));

        //when
        underTest.deleteCategory(1L);

        //then
        verify(categoryRepository).deleteById(1L);
    }

    @Test
    @DisplayName("Delete category with invalid Id")
    void canNotDeleteCategory() {
        //given
        given(categoryRepository.findById(any())).willReturn(Optional.empty());

        //when
        Long categoryId = 1L;

        //then
        assertThatThrownBy(() -> underTest.deleteCategory(categoryId))
                .isInstanceOf(CategoryNotFoundException.class);
    }
}