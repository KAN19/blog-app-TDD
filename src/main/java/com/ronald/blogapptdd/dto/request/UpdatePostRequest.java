package com.ronald.blogapptdd.dto.request;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class UpdatePostRequest {
    private String title;
    private String content;
    private Long categoryId;
    private List<Long> tagIds;
}
