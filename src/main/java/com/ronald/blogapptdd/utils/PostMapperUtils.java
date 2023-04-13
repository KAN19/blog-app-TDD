package com.ronald.blogapptdd.utils;

import com.ronald.blogapptdd.dto.response.PostResponseDTO;
import com.ronald.blogapptdd.entity.Post;
import com.ronald.blogapptdd.entity.Tag;

import java.util.ArrayList;
import java.util.List;

public class PostMapperUtils {

    public static PostResponseDTO mapPostToPostResponseDTO(Post post) {
        List<Tag> tags = new ArrayList<>();
        post.getPostTags().forEach(postTag -> tags.add(postTag.getTag()));
        return PostResponseDTO.builder()
                .id(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .category(post.getCategory())
                .createdAt(post.getCreatedAt())
                .tags(tags)
                .build();
    }

    public static List<PostResponseDTO> mapPostsToPostResponseDTOs(List<Post> posts) {
        List<PostResponseDTO> postResponseDTOS = new ArrayList<>();
        posts.forEach(post -> postResponseDTOS.add(mapPostToPostResponseDTO(post)));
        return postResponseDTOS;
    }

}
