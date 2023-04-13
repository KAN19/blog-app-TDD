package com.ronald.blogapptdd.dto.response;

import com.ronald.blogapptdd.entity.Category;
import com.ronald.blogapptdd.entity.PostTag;
import com.ronald.blogapptdd.entity.Tag;
import lombok.Builder;
import lombok.Data;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.List;

@Data
@Builder
public class PostResponseDTO {
    private Long id;
    private String title;
    private String content;
    private Category category;
    private Timestamp createdAt;
    private List<Tag> tags;

}
