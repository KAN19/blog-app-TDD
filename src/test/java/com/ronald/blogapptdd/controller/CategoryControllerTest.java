package com.ronald.blogapptdd.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ronald.blogapptdd.dto.request.CreateCategoryRequest;
import com.ronald.blogapptdd.dto.request.UpdateCategoryRequest;
import com.ronald.blogapptdd.entity.Category;
import com.ronald.blogapptdd.exception.CategoryNotFoundException;
import com.ronald.blogapptdd.service.CategoryService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CategoryService categoryService;

    @AfterEach
    void tearDown() {
    }

    @Test
    void canCreateCategoryHttpRequest() throws Exception {
        // given
        CreateCategoryRequest request = CreateCategoryRequest.builder()
                .name("Java")
                .description("Java ne")
                .build();
        given(categoryService.createCategory(request)).willReturn(Category.builder()
                .name(request.getName())
                .description(request.getDescription())
                .id(1L)
                .build());

        // when
        // then
        mockMvc.perform(post("/api/v1/category/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Java"))
                .andExpect(jsonPath("$.description").value("Java ne"));
    }

    @Test
    void canGetCategoryDetailHttpRequest() throws Exception {
        //given
        Category category = Category.builder()
                .name("Java")
                .description("Java ne")
                .id(1L)
                .build();
        given(categoryService.findCategoryById(1L)).willReturn(category);

        //when
        //then
        mockMvc.perform(get("/api/v1/category/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Java"))
                .andExpect(jsonPath("$.description").value("Java ne"));
    }

    @Test
    void canNotGetCategoryDetailHttpRequest() throws Exception {
        //given
        given(categoryService.findCategoryById(1L)).willThrow(new RuntimeException("Category not found"));

        //when
        //then
        mockMvc.perform(get("/api/v1/category/{id}", 1L))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$").value("Category not found"));
    }

    @Test
    void canGetAllCategoryHttpRequest() throws Exception {
        //given
        Category cate1 = Category.builder()
                .name("Java")
                .description("Java ne")
                .id(1L)
                .build();
        Category cate2 = Category.builder()
                .name("Python")
                .description("Python ne")
                .id(2L)
                .build();
        given(categoryService.findAllCategory()).willReturn(List.of(cate1, cate2));

        //when
        //then
        mockMvc.perform(get("/api/v1/category/"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    public void canUpdateCategoryHttpRequest() throws Exception {
        //given
        UpdateCategoryRequest request = UpdateCategoryRequest.builder()
                .name("Java")
                .description("Java ne")
                .build();
        given(categoryService.updateCategory(1L, request)).willReturn(Category.builder()
                .name(request.getName())
                .description(request.getDescription())
                .id(1L)
                .build());

        //when
        //then
        mockMvc.perform(put("/api/v1/category/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Java"))
                .andExpect(jsonPath("$.description").value("Java ne"));
    }

    @Test
    public void canNotUpdateCategoryHttpRequest() throws Exception {
        //given
        UpdateCategoryRequest request = UpdateCategoryRequest.builder()
                .name("Java")
                .description("Java ne")
                .build();
        given(categoryService.updateCategory(1L, request)).willThrow(new CategoryNotFoundException("Category not found"));

        //when
        //then
        mockMvc.perform(put("/api/v1/category/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$").value("Category not found"));
    }

    @Test
    public void canDeleteCategoryHttpRequest() throws Exception {
        //given
        //when
        //then
        mockMvc.perform(delete("/api/v1/category/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("Category deleted"));
    }

    @Test
    public void canNotDeleteCategoryHttpRequest() throws Exception {
        //given
        doThrow(new CategoryNotFoundException("Category not found")).when(categoryService).deleteCategory(1L);

        //when
        //then
        mockMvc.perform(delete("/api/v1/category/{id}", 1L))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$").value("Category not found"));
    }
}