package com.ronald.blogapptdd.service;

import com.ronald.blogapptdd.dto.request.CreateTagRequest;
import com.ronald.blogapptdd.dto.request.UpdateTagRequest;
import com.ronald.blogapptdd.entity.Tag;

import java.util.List;

public interface TagService {
    Tag createTag(CreateTagRequest request);

    List<Tag> getAllTags();

    Tag getTagDetail(Long tagId);

    Tag updateTag(Long tagId, UpdateTagRequest updateTagRequest);

    void deleteTag(Long tagId);
}
