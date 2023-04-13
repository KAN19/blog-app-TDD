package com.ronald.blogapptdd.service.impl;

import com.ronald.blogapptdd.dto.request.CreatePostRequest;
import com.ronald.blogapptdd.entity.Category;
import com.ronald.blogapptdd.entity.Post;
import com.ronald.blogapptdd.entity.PostTag;
import com.ronald.blogapptdd.entity.Tag;
import com.ronald.blogapptdd.entity.composite.PostTagKey;
import com.ronald.blogapptdd.exception.CategoryNotFoundException;
import com.ronald.blogapptdd.repository.CategoryRepository;
import com.ronald.blogapptdd.repository.PostRepository;
import com.ronald.blogapptdd.repository.PostTagRepository;
import com.ronald.blogapptdd.repository.TagRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@SpringBootTest
class PostServiceImplTest {

    @Autowired
    private PostServiceImpl underTest;

    @MockBean
    private PostRepository postRepository;

    @MockBean
    private PostTagRepository postTagRepository;

    @MockBean
    private TagRepository tagRepository;
    @MockBean
    private CategoryRepository categoryRepository;

    @BeforeEach
    void setUp() {
    }

    @Test
    public void canCreatePostWithExistingTagsAndCategory() {
        // given
        CreatePostRequest request = new CreatePostRequest("Test Title", "Test Content", 1L, Arrays.asList(1L, 2L));
        Category category = new Category(1L, "Test Category", "Test Description", new Timestamp(System.currentTimeMillis()));
        given(categoryRepository.findById(1L)).willReturn(Optional.of(category));
        given(tagRepository.findById(1L)).willReturn(Optional.of(Tag.builder()
                .id(1L)
                .name("K8s Series")
                .description("New series")
                .createdAt(new Timestamp(System.currentTimeMillis()))
                .build()));
        given(tagRepository.findById(2L)).willReturn(Optional.of(Tag.builder()
                .id(2L)
                .name("K8s Series 2")
                .description("New series 2")
                .createdAt(new Timestamp(System.currentTimeMillis()))
                .build()));


        PostTag postTag1 = PostTag.builder()
                .id(new PostTagKey(1L, 1L))
                .build();
        PostTag postTag2 = PostTag.builder()
                .id(new PostTagKey(1L, 2L))
                .build();
        List<PostTag> postTags = new ArrayList<>(Arrays.asList(postTag1, postTag2));
        given(postRepository.save(any(Post.class)))
                .willReturn(Post.builder()
                        .id(1L)
                        .title("Test Title")
                        .content("Test Content")
                        .category(category)
                        .postTags(postTags)
                        .createdAt(new Timestamp(System.currentTimeMillis()))
                        .build());
        given(postTagRepository.saveAll(anyList()))
                .willReturn(postTags);

        // when
        Post result = underTest.createPost(request);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getTitle()).isEqualTo("Test Title");
        assertThat(result.getContent()).isEqualTo("Test Content");
        assertThat(result.getCategory()).isEqualTo(category);
        assertThat(result.getPostTags()).hasSize(2);

        verify(categoryRepository, times(1)).findById(1L);
        verify(tagRepository, times(2)).findById(any());
        verify(postRepository, times(1)).save(any(Post.class));
        verify(postTagRepository, times(1)).saveAll(anyList());
    }

    @Test
    public void createPostWithNonExistingCategory() {
        // given
        CreatePostRequest request = new CreatePostRequest("Test Title", "Test Content", 1L, Arrays.asList(1L, 2L));
        given(categoryRepository.findById(1L)).willReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(() -> underTest.createPost(request))
                .isInstanceOf(CategoryNotFoundException.class)
                .hasMessageContaining("Category not found");
        verify(categoryRepository, times(1)).findById(1L);
        verifyNoMoreInteractions(categoryRepository, tagRepository, postRepository, postTagRepository);
    }

    @Test
    public void createPostWithNonExistingTag() {
        // given
        CreatePostRequest request = CreatePostRequest.builder()
                .title("Test Post")
                .content("This is a test post")
                .categoryId(1L)
                .tagIds(Arrays.asList(99L, 100L)) // Non-existing tags
                .build();

        Category category = Category.builder()
                .id(1L)
                .name("Test Category")
                .build();
        given(categoryRepository.findById(1L)).willReturn(Optional.of(category));

        Post savedPost = Post.builder()
                .id(1L)
                .title("Test Post")
                .content("This is a test post")
                .category(category)
                .createdAt(new Timestamp(System.currentTimeMillis()))
                .postTags(new ArrayList<>())
                .build();
        given(postRepository.save(any(Post.class))).willReturn(savedPost);


        // when
        Post post = underTest.createPost(request);

        // then
        assertThat(post).isNotNull();
        assertThat(post.getTitle()).isEqualTo("Test Post");
        assertThat(post.getContent()).isEqualTo("This is a test post");
        assertThat(post.getCategory()).isEqualTo(category);
        assertThat(post.getPostTags()).isEmpty();

        verify(categoryRepository, times(1)).findById(1L);
        verify(postRepository, times(1)).save(any(Post.class));
        verify(tagRepository, times(2)).findById(any());
        verifyNoMoreInteractions(categoryRepository, postRepository, postTagRepository);
    }

    @Test
    public void createPostWithEmptyTagList() {
        // given
        CreatePostRequest request = CreatePostRequest.builder()
                .title("Test Post")
                .content("This is a test post")
                .categoryId(1L)
                .tagIds(new ArrayList<>())
                .build();

        Category category = Category.builder()
                .id(1L)
                .name("Test Category")
                .build();
        given(categoryRepository.findById(1L)).willReturn(Optional.of(category));

        Post savedPost = Post.builder()
                .id(1L)
                .title("Test Post")
                .content("This is a test post")
                .category(category)
                .createdAt(new Timestamp(System.currentTimeMillis()))
                .postTags(new ArrayList<>())
                .build();
        given(postRepository.save(any(Post.class))).willReturn(savedPost);

        // when
        Post post = underTest.createPost(request);

        // then
        assertThat(post).isNotNull();
        assertThat(post.getTitle()).isEqualTo("Test Post");
        assertThat(post.getContent()).isEqualTo("This is a test post");
        assertThat(post.getCategory()).isEqualTo(category);
        assertThat(post.getPostTags()).isEmpty();

        verify(categoryRepository, times(1)).findById(1L);
        verify(postRepository, times(1)).save(any(Post.class));
    }

    @AfterEach
    void tearDown() {
    }
}