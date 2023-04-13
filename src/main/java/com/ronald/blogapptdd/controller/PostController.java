package com.ronald.blogapptdd.controller;

import com.ronald.blogapptdd.dto.request.CreatePostRequest;
import com.ronald.blogapptdd.dto.request.UpdatePostRequest;
import com.ronald.blogapptdd.dto.response.PostResponseDTO;
import com.ronald.blogapptdd.entity.Post;
import com.ronald.blogapptdd.service.PostService;
import com.ronald.blogapptdd.utils.PostMapperUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/post")
public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping("/")
    public ResponseEntity<?> getAllPost() {
        List<PostResponseDTO> allPosts = postService.getAllPosts();
        return ResponseEntity.ok(allPosts);
    }

    @PostMapping("/")
    public ResponseEntity<?> createPost(@RequestBody CreatePostRequest request) {
        Post post = postService.createPost(request);
        PostResponseDTO postResponseDTO = PostMapperUtils.mapPostToPostResponseDTO(post);
        return ResponseEntity.ok(postResponseDTO);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getPostDetail(@PathVariable Long id) {
        PostResponseDTO postDetail = postService.getPostDetail(id);
        return ResponseEntity.ok(postDetail);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updatePost(@PathVariable Long id, UpdatePostRequest request) {
        PostResponseDTO post = postService.updatePost(id, request);
        return ResponseEntity.ok(post);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePost(@PathVariable Long id) {
        postService.deletePost(id);
        return ResponseEntity.ok("Post deleted");
    }
}
