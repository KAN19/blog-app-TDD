package com.ronald.blogapptdd.service.impl;

import com.ronald.blogapptdd.dto.request.CreateTagRequest;
import com.ronald.blogapptdd.dto.request.UpdateTagRequest;
import com.ronald.blogapptdd.entity.Tag;
import com.ronald.blogapptdd.exception.TagNotFoundException;
import com.ronald.blogapptdd.repository.TagRepository;
import com.ronald.blogapptdd.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TagServiceImpl implements TagService {

    private final TagRepository tagRepository;

    @Autowired
    public TagServiceImpl(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }


    @Override
    public Tag createTag(CreateTagRequest request) {
        Tag tag = Tag.builder()
                .name(request.getName())
                .description(request.getDescription())
                .build();
        return tagRepository.save(tag);
    }

    @Override
    public List<Tag> getAllTags() {
        return tagRepository.findAll();
    }

    @Override
    public Tag getTagDetail(Long tagId) {
        Tag tag = tagRepository.findById(tagId)
                .orElseThrow(() -> new TagNotFoundException("Tag not found with id: " + tagId));
        return tag;
    }

    @Override
    public Tag updateTag(Long tagId, UpdateTagRequest updateTagRequest) {
        Tag tag = tagRepository.findById(tagId)
                .orElseThrow(() -> new TagNotFoundException("Tag not found with id: " + tagId));

        Tag updatedTag = compareAndUpdate(tag, updateTagRequest);

        return tagRepository.save(updatedTag);
    }

    @Override
    public void deleteTag(Long tagId) {
        tagRepository.findById(tagId)
                .orElseThrow(() -> new TagNotFoundException("Tag not found with id: " + tagId));

        tagRepository.deleteById(tagId);
    }

    private Tag compareAndUpdate(Tag tag, UpdateTagRequest updateTagRequest) {
        if (updateTagRequest.getName() != null) {
            tag.setName(updateTagRequest.getName());
        }
        if (updateTagRequest.getDescription() != null) {
            tag.setDescription(updateTagRequest.getDescription());
        }
        return tag;
    }
}
