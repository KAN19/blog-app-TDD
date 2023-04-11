package com.ronald.blogapptdd.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ronald.blogapptdd.dto.request.CreateTagRequest;
import com.ronald.blogapptdd.dto.request.UpdateTagRequest;
import com.ronald.blogapptdd.entity.Tag;
import com.ronald.blogapptdd.service.TagService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class TagControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TagService tagService;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
    }

    @Test
    public void canCreateTagHttpRequest() throws Exception {
        // given
        CreateTagRequest request = CreateTagRequest.builder()
                .name("Java")
                .build();
        Tag tag = Tag.builder()
                .name(request.getName())
                .id(1L)
                .build();
        given(tagService.createTag(request)).willReturn(tag);

        // when
        // then
        mockMvc.perform(post("/api/v1/tag/")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(request.getName()));
    }

    @Test
    public void canGetAllTagsHttpRequest() throws Exception {
        // given
        Tag tag1 = Tag.builder()
                .name("Java")
                .id(1L)
                .build();
        Tag tag2 = Tag.builder()
                .name("Spring")
                .id(2L)
                .build();
        given(tagService.getAllTags()).willReturn(List.of(tag1, tag2));

        // when
        // then
        mockMvc.perform(get("/api/v1/tag/"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    public void canGetTagByIdHttpRequest() throws Exception {
        // given
        Tag tag = Tag.builder()
                .name("Java")
                .id(1L)
                .build();
        given(tagService.getTagDetail(1L)).willReturn(tag);

        // when
        // then
        mockMvc.perform(get("/api/v1/tag/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(tag.getName()));
    }

    @Test
    public void cannotGetTagByIdHttpRequest() throws Exception {
        // given
        given(tagService.getTagDetail(1L)).willThrow(new RuntimeException());

        // when
        // then
        mockMvc.perform(get("/api/v1/tag/{id}", 1L))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void canUpdateTagHttpRequest() throws Exception {
        // given
        UpdateTagRequest request = UpdateTagRequest.builder()
                .name("Java")
                .build();
        Tag tag = Tag.builder()
                .name(request.getName())
                .id(1L)
                .build();
        given(tagService.updateTag(1L, request)).willReturn(tag);

        // when
        // then
        mockMvc.perform(put("/api/v1/tag/{id}", 1L)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(request.getName()));
    }

    @Test
    public void canDeleteTagHttpRequest() throws Exception {
        // given
        doNothing().when(tagService).deleteTag(1L);

        // when
        // then
        mockMvc.perform(delete("/api/v1/tag/{id}", 1L))
                .andExpect(status().isOk());
    }

    @AfterEach
    void tearDown() {
    }
}