package com.ronald.blogapptdd.controller;

import com.ronald.blogapptdd.dto.request.CreateTagRequest;
import com.ronald.blogapptdd.dto.request.UpdateTagRequest;
import com.ronald.blogapptdd.entity.Tag;
import com.ronald.blogapptdd.service.TagService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/tag")
public class TagController {

    private final TagService tagService;

    public TagController(TagService tagService) {
        this.tagService = tagService;
    }

    @GetMapping("/")
    public ResponseEntity<?> getAllTag() {
        return ResponseEntity.ok(tagService.getAllTags());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getTagDetail(@PathVariable Long id) {
        Tag tagDetail = tagService.getTagDetail(id);
        return ResponseEntity.ok(tagDetail);
    }

    @PostMapping("/")
    public ResponseEntity<?> createTag(@RequestBody CreateTagRequest request) {
        Tag newTag = tagService.createTag(request);
        return ResponseEntity.ok(newTag);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateTag(@PathVariable Long id, @RequestBody UpdateTagRequest request) {
        Tag updatedTag = tagService.updateTag(id, request);
        return ResponseEntity.ok(updatedTag);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTag(@PathVariable Long id) {
        tagService.deleteTag(id);
        return ResponseEntity.ok("Tag deleted");
    }
}
