package com.ronald.blogapptdd.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ronald.blogapptdd.dto.request.CreatePostRequest;
import com.ronald.blogapptdd.dto.request.UpdatePostRequest;
import com.ronald.blogapptdd.dto.response.PostResponseDTO;
import com.ronald.blogapptdd.entity.Category;
import com.ronald.blogapptdd.entity.Post;
import com.ronald.blogapptdd.entity.PostTag;
import com.ronald.blogapptdd.entity.Tag;
import com.ronald.blogapptdd.entity.composite.PostTagKey;
import com.ronald.blogapptdd.service.PostService;
import com.ronald.blogapptdd.utils.PostMapperUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@SpringBootTest
@AutoConfigureMockMvc
class PostControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PostService postService;

    @Test
    void canCreatePostHttpRequest() throws Exception {
        //given
        CreatePostRequest request = CreatePostRequest.builder()
                .title("This title talks about Java")
                .content("This content talks about Java")
                .build();
        Category category = Category.builder()
                .id(1L)
                .name("Java")
                .description("Java ne")
                .build();

        Post result = Post.builder()
                .id(1L)
                .title(request.getTitle())
                .content(request.getContent())
                .category(category)
                .build();

        Tag tag = Tag.builder()
                .id(1L)
                .name("Java Tag")
                .description("Java ne")
                .build();
        PostTag postTag = PostTag.builder()
                .id(new PostTagKey(1L, 1L))
                .post(result)
                .tag(tag)
                .build();
        result.setPostTags(List.of(postTag));


        given(postService.createPost(request))
                .willReturn(result);

        //when
        //then
        mockMvc.perform(post("/api/v1/post/")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.title").value(request.getTitle()))
                .andExpect(jsonPath("$.content").value(request.getContent()))
                .andExpect(jsonPath("$.category.id").value(category.getId()))
                .andExpect(jsonPath("$.category.name").value(category.getName()))
                .andExpect(jsonPath("$.category.description").value(category.getDescription()))
                .andExpect(jsonPath("$.tags[0].id").value(1L));
    }

    @Test
    public void getAllPostHttpRequest() throws Exception {
        //given
        Category category = Category.builder()
                .id(1L)
                .name("Java")
                .description("Java ne")
                .build();

        Post post1 = Post.builder()
                .id(1L)
                .title("This title talks about Java")
                .content("This content talks about Java")
                .category(category)
                .build();

        Post post2 = Post.builder()
                .id(2L)
                .title("This title talks about Python")
                .content("This content talks about Python")
                .category(category)
                .build();

        Tag tag = Tag.builder()
                .id(1L)
                .name("Java Tag")
                .description("Java ne")
                .build();

        Tag tag2 = Tag.builder()
                .id(2L)
                .name("Python Tag")
                .description("Python ne")
                .build();

        PostTag postTag = PostTag.builder()
                .id(new PostTagKey(1L, 1L))
                .post(post1)
                .tag(tag)
                .build();
        PostTag postTag2 = PostTag.builder()
                .id(new PostTagKey(2L, 2L))
                .post(post2)
                .tag(tag2)
                .build();

        post1.setPostTags(List.of(postTag));
        post2.setPostTags(List.of(postTag2));

        List<PostResponseDTO> postResponseDTOS = PostMapperUtils
                .mapPostsToPostResponseDTOs(List.of(post1, post2));

        given(postService.getAllPosts())
                .willReturn(postResponseDTOS);

        //when
        //then
        mockMvc.perform(get("/api/v1/post/"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].title").value(post1.getTitle()))
                .andExpect(jsonPath("$[0].content").value(post1.getContent()))
                .andExpect(jsonPath("$[0].category.id").value(category.getId()))
                .andExpect(jsonPath("$[0].category.name").value(category.getName()))
                .andExpect(jsonPath("$[0].category.description").value(category.getDescription()))
                .andExpect(jsonPath("$[0].tags[0].id").value(1L))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].title").value(post2.getTitle()))
                .andExpect(jsonPath("$[1].content").value(post2.getContent()))
                .andExpect(jsonPath("$[1].category.id").value(category.getId()))
                .andExpect(jsonPath("$[1].category.name").value(category.getName()))
                .andExpect(jsonPath("$[1].category.description").value(category.getDescription()))
                .andExpect(jsonPath("$[1].tags[0].id").value(2L));
    }

    @Test
    public void canGetPostDetailHttpRequest() throws Exception {
        //given
        Category category = Category.builder()
                .id(1L)
                .name("Java")
                .description("Java ne")
                .build();

        Post post = Post.builder()
                .id(1L)
                .title("This title talks about Java")
                .content("This content talks about Java")
                .category(category)
                .build();

        Tag tag = Tag.builder()
                .id(1L)
                .name("Java Tag")
                .description("Java ne")
                .build();

        PostTag postTag = PostTag.builder()
                .id(new PostTagKey(1L, 1L))
                .post(post)
                .tag(tag)
                .build();

        post.setPostTags(List.of(postTag));

        PostResponseDTO postResponseDTO = PostMapperUtils
                .mapPostToPostResponseDTO(post);

        given(postService.getPostDetail(1L))
                .willReturn(postResponseDTO);

        //when
        //then
        mockMvc.perform(get("/api/v1/post/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.title").value(post.getTitle()))
                .andExpect(jsonPath("$.content").value(post.getContent()))
                .andExpect(jsonPath("$.category.id").value(category.getId()))
                .andExpect(jsonPath("$.category.name").value(category.getName()))
                .andExpect(jsonPath("$.category.description").value(category.getDescription()))
                .andExpect(jsonPath("$.tags[0].id").value(1L));
    }

    @Test
    public void canUpdatePostHttpRequest() throws Exception {
        // given
        UpdatePostRequest request = UpdatePostRequest.builder()
                .title("This title talks about Java")
                .content("This content talks about Java")
                .categoryId(2L)
                .tagIds(List.of(1L, 2L))
                .build();

        Category newUpdatedCategory = Category.builder()
                .id(2L)
                .name("Python")
                .description("Python ne")
                .build();

        Post newUpdatedPost = Post.builder()
                .id(1L)
                .title("This title talks about Java")
                .content("This content talks about Java")
                .category(newUpdatedCategory)
                .build();

        Tag tag = Tag.builder()
                .id(1L)
                .name("Java Tag")
                .description("Java ne")
                .build();
        Tag tag2 = Tag.builder()
                .id(2L)
                .name("Python Tag")
                .description("Python ne")
                .build();

        PostTag postTag = PostTag.builder()
                .id(new PostTagKey(1L, 1L))
                .post(newUpdatedPost)
                .tag(tag)
                .build();

        PostTag newUpdatedPostTag = PostTag.builder()
                .id(new PostTagKey(1L, 2L))
                .post(newUpdatedPost)
                .tag(tag2)
                .build();

        newUpdatedPost.setPostTags(List.of(postTag, newUpdatedPostTag));

        PostResponseDTO postResponseDTO = PostMapperUtils
                .mapPostToPostResponseDTO(newUpdatedPost);

        given(postService.updatePost(any(), any())).willReturn(postResponseDTO);

        // when
        // then
        mockMvc.perform(put("/api/v1/post/{id}", 1L)
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.title").value(newUpdatedPost.getTitle()))
                .andExpect(jsonPath("$.content").value(newUpdatedPost.getContent()))
                .andExpect(jsonPath("$.category.id").value(newUpdatedCategory.getId()))
                .andExpect(jsonPath("$.category.name").value(newUpdatedCategory.getName()))
                .andExpect(jsonPath("$.category.description").value(newUpdatedCategory.getDescription()))
                .andExpect(jsonPath("$.tags[0].id").value(1L))
                .andExpect(jsonPath("$.tags[1].id").value(2L));
    }

    @Test
    public void canDeletePostHttpRequest() throws Exception {
        // given
        doNothing().when(postService).deletePost(any());

        // when
        // then
        mockMvc.perform(delete("/api/v1/post/{id}", 1L))
                .andExpect(status().isOk());
    }

    @AfterEach
    void tearDown() {
    }

}