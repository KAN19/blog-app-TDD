package com.ronald.blogapptdd.service.impl;

import com.ronald.blogapptdd.dto.request.CreatePostRequest;
import com.ronald.blogapptdd.entity.Category;
import com.ronald.blogapptdd.entity.Post;
import com.ronald.blogapptdd.entity.PostTag;
import com.ronald.blogapptdd.entity.composite.PostTagKey;
import com.ronald.blogapptdd.exception.CategoryNotFoundException;
import com.ronald.blogapptdd.repository.CategoryRepository;
import com.ronald.blogapptdd.repository.PostRepository;
import com.ronald.blogapptdd.repository.PostTagRepository;
import com.ronald.blogapptdd.repository.TagRepository;
import com.ronald.blogapptdd.service.PostService;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

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
}
