package com.ronald.blogapptdd.service.impl;

import com.ronald.blogapptdd.dto.request.CreatePostRequest;
import com.ronald.blogapptdd.dto.request.UpdatePostRequest;
import com.ronald.blogapptdd.dto.response.PostResponseDTO;
import com.ronald.blogapptdd.entity.Category;
import com.ronald.blogapptdd.entity.Post;
import com.ronald.blogapptdd.entity.PostTag;
import com.ronald.blogapptdd.entity.Tag;
import com.ronald.blogapptdd.entity.composite.PostTagKey;
import com.ronald.blogapptdd.exception.CategoryNotFoundException;
import com.ronald.blogapptdd.exception.PostNotFoundException;
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

    @Test
    public void getAllPostWithoutTag() {
        // given
        Category category = Category.builder()
                .id(1L)
                .name("Test Category")
                .build();
        Post post1 = Post.builder()
                .id(1L)
                .title("Test Post 1")
                .content("This is a test post 1")
                .category(category)
                .createdAt(new Timestamp(System.currentTimeMillis()))
                .postTags(new ArrayList<>())
                .build();
        Post post2 = Post.builder()
                .id(2L)
                .title("Test Post 2")
                .content("This is a test post 2")
                .category(category)
                .createdAt(new Timestamp(System.currentTimeMillis()))
                .postTags(new ArrayList<>())
                .build();
        given(postRepository.findAll()).willReturn(Arrays.asList(post1, post2));

        // when
        List<PostResponseDTO> allPosts = underTest.getAllPosts();

        // then
        assertThat(allPosts).isNotNull();
        assertThat(allPosts).hasSize(2);
        assertThat(allPosts.get(0).getTitle()).isEqualTo("Test Post 1");
        assertThat(allPosts.get(0).getContent()).isEqualTo("This is a test post 1");
        assertThat(allPosts.get(0).getCategory().getName()).isEqualTo("Test Category");
        assertThat(allPosts.get(0).getTags()).isEmpty();
        assertThat(allPosts.get(1).getTitle()).isEqualTo("Test Post 2");
        assertThat(allPosts.get(1).getContent()).isEqualTo("This is a test post 2");
        assertThat(allPosts.get(1).getCategory().getName()).isEqualTo("Test Category");
        assertThat(allPosts.get(1).getTags()).isEmpty();

        verify(postRepository, times(1)).findAll();
    }

    @Test
    public void getAllPostWithTag() {
        // given
        Category category = Category.builder()
                .id(1L)
                .name("Test Category")
                .build();
        Post post1 = Post.builder()
                .id(1L)
                .title("Test Post 1")
                .content("This is a test post 1")
                .category(category)
                .createdAt(new Timestamp(System.currentTimeMillis()))
                .postTags(new ArrayList<>())
                .build();
        Post post2 = Post.builder()
                .id(2L)
                .title("Test Post 2")
                .content("This is a test post 2")
                .category(category)
                .createdAt(new Timestamp(System.currentTimeMillis()))
                .postTags(new ArrayList<>())
                .build();

        Tag tag1 = Tag.builder()
                .id(1L)
                .name("Test Tag 1")
                .build();
        Tag tag2 = Tag.builder()
                .id(2L)
                .name("Test Tag 2")
                .build();

        PostTag postTag1 = PostTag.builder()
                .id(new PostTagKey(1L, 1L))
                .post(post1)
                .tag(tag1)
                .build();
        PostTag postTag2 = PostTag.builder()
                .id(new PostTagKey(1L, 2L))
                .post(post1)
                .tag(tag2)
                .build();
        PostTag postTag3 = PostTag.builder()
                .id(new PostTagKey(2L, 1L))
                .post(post2)
                .tag(tag1)
                .build();

        post1.setPostTags(Arrays.asList(postTag1, postTag2));
        post2.setPostTags(Arrays.asList(postTag3));

        given(postRepository.findAll()).willReturn(Arrays.asList(post1, post2));

        // when
        List<PostResponseDTO> allPosts = underTest.getAllPosts();

        // then
        assertThat(allPosts).isNotNull();
        assertThat(allPosts).hasSize(2);
        assertThat(allPosts.get(0).getTitle()).isEqualTo("Test Post 1");
        assertThat(allPosts.get(0).getContent()).isEqualTo("This is a test post 1");
        assertThat(allPosts.get(0).getCategory().getName()).isEqualTo("Test Category");
        assertThat(allPosts.get(0).getTags()).hasSize(2);
        assertThat(allPosts.get(0).getTags().get(0).getName()).isEqualTo("Test Tag 1");
        assertThat(allPosts.get(0).getTags().get(1).getName()).isEqualTo("Test Tag 2");
        assertThat(allPosts.get(1).getTitle()).isEqualTo("Test Post 2");
        assertThat(allPosts.get(1).getContent()).isEqualTo("This is a test post 2");
        assertThat(allPosts.get(1).getCategory().getName()).isEqualTo("Test Category");
        assertThat(allPosts.get(1).getTags()).hasSize(1);
        assertThat(allPosts.get(1).getTags().get(0).getName()).isEqualTo("Test Tag 1");

        verify(postRepository, times(1)).findAll();
    }

    @Test
    public void updatePostWhenPostFound() {
        // given
        UpdatePostRequest request = UpdatePostRequest.builder()
                .title("Test Post Updated")
                .content("This is a test post updated")
                .categoryId(2L)
                .tagIds(Arrays.asList(1L))
                .build();
        Category updatedCategory = Category.builder()
                .id(2L)
                .name("Test Category Updated")
                .build();
        given(categoryRepository.findById(2L)).willReturn(Optional.of(updatedCategory));

        Category category = Category.builder()
                .id(1L)
                .name("Test Category")
                .build();
        given(categoryRepository.findById(1L)).willReturn(Optional.of(category));

        Post post = Post.builder()
                .id(1L)
                .title("Test Post")
                .content("This is a test post")
                .category(category)
                .createdAt(new Timestamp(System.currentTimeMillis()))
                .postTags(new ArrayList<>())
                .build();

        Tag tag = Tag.builder()
                .id(1L)
                .name("Test Tag")
                .build();
        Tag tag2 = Tag.builder()
                .id(2L)
                .name("Test Tag 2")
                .build();
        PostTag postTag = PostTag.builder()
                .id(new PostTagKey(1L, 1L))
                .post(post)
                .tag(tag)
                .build();
        PostTag postTag2 = PostTag.builder()
                .id(new PostTagKey(1L, 2L))
                .post(post)
                .tag(tag2)
                .build();
        post.setPostTags(Arrays.asList(postTag, postTag2));

        given(postRepository.findById(1L)).willReturn(Optional.of(post));

        Post savedPost = Post.builder()
                .id(1L)
                .title("Test Post Updated")
                .content("This is a test post updated")
                .category(updatedCategory)
                .createdAt(new Timestamp(System.currentTimeMillis()))
                .postTags(Arrays.asList(postTag))
                .build();
        given(postRepository.save(any(Post.class))).willReturn(savedPost);

        given(tagRepository.findByIdIn(List.of(1L))).willReturn(Arrays.asList(tag));

        // when
        PostResponseDTO updatedPost = underTest.updatePost(1L, request);

        // then
        assertThat(updatedPost).isNotNull();
        assertThat(updatedPost.getTitle()).isEqualTo("Test Post Updated");
        assertThat(updatedPost.getContent()).isEqualTo("This is a test post updated");
        assertThat(updatedPost.getCategory()).isEqualTo(updatedCategory);
        assertThat(updatedPost.getTags()).hasSize(1);
        assertThat(updatedPost.getTags().get(0).getName()).isEqualTo("Test Tag");

        verify(postRepository, times(1)).findById(1L);
        verify(postRepository, times(1)).save(any(Post.class));
    }

    @Test
    public void updatePostWhenPostNotFoundThenThrowException() {
        // given
        UpdatePostRequest request = UpdatePostRequest.builder()
                .title("Test Post")
                .content("This is a test post")
                .categoryId(1L)
                .tagIds(Arrays.asList(1L))
                .build();

        given(postRepository.findById(1L)).willReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(() -> underTest.updatePost(1L, request))
                .isInstanceOf(PostNotFoundException.class)
                .hasMessage("Post not found");

        verify(postRepository, times(1)).findById(1L);
        verify(postRepository, times(0)).save(any(Post.class));
    }

    @Test
    public void getPostDetail() {
        // given
        Category category = Category.builder()
                .id(1L)
                .name("Test Category")
                .build();

        Post post = Post.builder()
                .id(1L)
                .title("Test Post")
                .content("This is a test post")
                .category(category)
                .createdAt(new Timestamp(System.currentTimeMillis()))
                .postTags(new ArrayList<>())
                .build();

        Tag tag = Tag.builder()
                .id(1L)
                .name("Test Tag")
                .build();
        Tag tag2 = Tag.builder()
                .id(2L)
                .name("Test Tag 2")
                .build();
        PostTag postTag = PostTag.builder()
                .id(new PostTagKey(1L, 1L))
                .post(post)
                .tag(tag)
                .build();
        PostTag postTag2 = PostTag.builder()
                .id(new PostTagKey(1L, 2L))
                .post(post)
                .tag(tag2)
                .build();
        post.setPostTags(Arrays.asList(postTag, postTag2));

        given(postRepository.findById(1L)).willReturn(Optional.of(post));

        // when
        PostResponseDTO postDetail = underTest.getPostDetail(1L);

        // then
        assertThat(postDetail).isNotNull();
        assertThat(postDetail.getTitle()).isEqualTo("Test Post");
        assertThat(postDetail.getContent()).isEqualTo("This is a test post");
        assertThat(postDetail.getCategory()).isEqualTo(category);
        assertThat(postDetail.getTags()).hasSize(2);
        assertThat(postDetail.getTags().get(0).getName()).isEqualTo("Test Tag");
        assertThat(postDetail.getTags().get(1).getName()).isEqualTo("Test Tag 2");

        verify(postRepository, times(1)).findById(1L);
    }

    @Test
    public void getPostDetailNotFoundPostException() {
        // given
        given(postRepository.findById(1L)).willReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(() -> underTest.getPostDetail(1L))
                .isInstanceOf(PostNotFoundException.class)
                .hasMessage("Post not found");

        verify(postRepository, times(1)).findById(1L);
    }

    @Test
    public void deletePost() {
        // given
        Category category = Category.builder()
                .id(1L)
                .name("Test Category")
                .description("Test Category Description")
                .createdAt(new Timestamp(System.currentTimeMillis()))
                .build();

        Tag tag = Tag.builder()
                .id(1L)
                .name("Test Tag")
                .description("Test Tag Description")
                .createdAt(new Timestamp(System.currentTimeMillis()))
                .build();
        Tag tag2 = Tag.builder()
                .id(2L)
                .name("Test Tag 2")
                .description("Test Tag 2 Description")
                .createdAt(new Timestamp(System.currentTimeMillis()))
                .build();

        Post post = Post.builder()
                .id(1L)
                .title("Test Post")
                .content("This is a test post")
                .category(category)
                .createdAt(new Timestamp(System.currentTimeMillis()))
                .postTags(new ArrayList<>())
                .build();

        PostTag postTag = PostTag.builder()
                .id(new PostTagKey(1L, 1L))
                .post(post)
                .tag(tag)
                .build();
        PostTag postTag2 = PostTag.builder()
                .id(new PostTagKey(1L, 2L))
                .post(post)
                .tag(tag2)
                .build();
        post.setPostTags(Arrays.asList(postTag, postTag2));

        given(postRepository.findById(1L)).willReturn(Optional.of(post));

        // when
        underTest.deletePost(1L);

        // then
        verify(postRepository, times(1)).deleteById(1L);
        verify(postTagRepository, times(1)).deleteAll(post.getPostTags());
    }

    @AfterEach
    void tearDown() {
    }
}