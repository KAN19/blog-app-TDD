package com.ronald.blogapptdd.service;

import com.ronald.blogapptdd.dto.request.CreatePostRequest;
import com.ronald.blogapptdd.dto.request.UpdatePostRequest;
import com.ronald.blogapptdd.dto.response.PostResponseDTO;
import com.ronald.blogapptdd.entity.Post;

import java.util.List;

public interface PostService {
    Post createPost(CreatePostRequest request);

    List<PostResponseDTO> getAllPosts();

    PostResponseDTO updatePost(Long postId, UpdatePostRequest request);

    PostResponseDTO getPostDetail(Long postId);

    void deletePost(Long postId);

}
