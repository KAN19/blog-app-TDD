package com.ronald.blogapptdd.dto.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreateTagRequest {
    private String name;
    private String description;
}
