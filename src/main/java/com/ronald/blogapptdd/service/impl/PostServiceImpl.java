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
import com.ronald.blogapptdd.service.PostService;
import com.ronald.blogapptdd.utils.PostMapperUtils;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;

    private final CategoryRepository categoryRepository;

    private final TagRepository tagRepository;

    private final PostTagRepository postTagRepository;

    public PostServiceImpl(PostRepository postRepository, CategoryRepository categoryRepository, TagRepository tagRepository, PostTagRepository postTagRepository) {
        this.postRepository = postRepository;
        this.categoryRepository = categoryRepository;
        this.tagRepository = tagRepository;
        this.postTagRepository = postTagRepository;
    }


    @Override
    @Transactional
    public Post createPost(CreatePostRequest request) {

        Category category = categoryRepository.findById(request.getCategoryId()).orElseThrow(() -> new CategoryNotFoundException("Category not found"));

        Post post = Post.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .category(category)
                .build();

        List<PostTag> postTags = new ArrayList<>();
        if (request.getTagIds() != null) {
            request.getTagIds()
                    .forEach(tagId -> tagRepository.findById(tagId)
                            .ifPresent(tag -> postTags.add(PostTag.builder()
                                    .id(new PostTagKey(post.getId(), tagId)).build())));
        }
        if (!postTags.isEmpty()) {
            postTagRepository.saveAll(postTags);
        }
        return postRepository.save(post);
    }

    @Override
    public List<PostResponseDTO> getAllPosts() {
        List<Post> allPosts = postRepository.findAll();
        return PostMapperUtils.mapPostsToPostResponseDTOs(allPosts);
    }

    @Override
    public PostResponseDTO updatePost(Long postId, UpdatePostRequest request) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException("Post not found"));

        if (!request.getTitle().equals(post.getTitle())) {
            post.setTitle(request.getTitle());
        }
        if (!request.getContent().equals(post.getContent())) {
            post.setContent(request.getContent());
        }
        if (!request.getCategoryId().equals(post.getCategory().getId())) {
            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new CategoryNotFoundException("Category not found"));
            post.setCategory(category);
        }

        List<PostTag> postTags = post.getPostTags();
        List<Tag> tags = tagRepository.findByIdIn(request.getTagIds());
        List<PostTag> newPostTags = tags.stream()
                .map(tag -> PostTag.builder()
                        .id(new PostTagKey(postId, tag.getId()))
                        .build())
                .collect(Collectors.toList());
        postTagRepository.deleteAll(postTags);
        if (!newPostTags.isEmpty()) {
            postTagRepository.saveAll(newPostTags);
        }

        Post savedPost = postRepository.save(post);
        return PostMapperUtils.mapPostToPostResponseDTO(savedPost);
    }
}
