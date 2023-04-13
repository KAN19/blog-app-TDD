package com.ronald.blogapptdd.dto.response;

import com.ronald.blogapptdd.entity.PostTag;
import lombok.Builder;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.OneToMany;
import java.sql.Timestamp;
import java.util.List;

@Data
@Builder
public class TagResponseDTO {
    private Long id;
    private String name;
    private String description;
    private Timestamp createdAt;
}
