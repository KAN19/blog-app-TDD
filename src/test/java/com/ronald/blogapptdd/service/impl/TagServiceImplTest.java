package com.ronald.blogapptdd.service.impl;

import com.ronald.blogapptdd.dto.request.CreateTagRequest;
import com.ronald.blogapptdd.dto.request.UpdateTagRequest;
import com.ronald.blogapptdd.entity.Tag;
import com.ronald.blogapptdd.exception.TagNotFoundException;
import com.ronald.blogapptdd.repository.TagRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.assertj.core.api.Java6Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest
class TagServiceImplTest {

    @Autowired
    private TagServiceImpl underTest;

    @MockBean
    private TagRepository tagRepository;

    private Tag tag;

    private CreateTagRequest request;

    @BeforeEach
    void setUp() {
        request = CreateTagRequest.builder()
                .description("My Duyen xinh dep")
                .name("My Duyen")
                .build();

        tag = Tag.builder()
                .description(request.getDescription())
                .postTags(new ArrayList<>())
                .id(1L)
                .name(request.getName())
                .build();

    }

    @Test
    public void canCreateTag() {
        //when
        underTest.createTag(request);

        //then
        ArgumentCaptor<Tag> tagArgumentCaptor = ArgumentCaptor.forClass(Tag.class);
        verify(tagRepository).save(tagArgumentCaptor.capture());

        Tag capturedTag = tagArgumentCaptor.getValue();
        assertThat(capturedTag).isEqualToComparingOnlyGivenFields(tag, "description", "name");
    }

    @Test
    public void canGetAllTag() {
        Tag tag2 = Tag.builder()
                .name("K8s")
                .description("Devops")
                .build();

        given(tagRepository.findAll()).willReturn(List.of(tag, tag2));

        //when
        List<Tag> tags = underTest.getAllTags();

        //then
        assertThat(tags).hasSize(2);
        verify(tagRepository, times(1)).findAll();
    }

    @Test
    public void getTagDetail() {
        //given
        Long tagId = 1L;
        given(tagRepository.findById(tagId)).willReturn(Optional.ofNullable(tag));

        Tag tagDetail = underTest.getTagDetail(tagId);

        assertThat(tagDetail).isEqualToComparingOnlyGivenFields(tag, "name", "description");
        verify(tagRepository).findById(1L);
    }

    @Test
    public void canNotGetTagDetailBecauseOfNotFound() {
        given(tagRepository.findById(any(Long.class))).willReturn(Optional.empty());

        assertThatThrownBy(() -> underTest.getTagDetail(1L))
                .isInstanceOf(TagNotFoundException.class)
                .hasMessageContaining("Tag not found with id: " + 1L);
    }

    @Test
    public void canUpdateTag() {
        //given
        Long tagId = 1L;
        UpdateTagRequest requestUpdate = UpdateTagRequest.builder()
                .description("My Duyen cute")
                .name("Cloud").build();
        Tag updatedCategory = Tag.builder()
                .description(requestUpdate.getDescription())
                .name(requestUpdate.getName()).build();
        given(tagRepository.findById(tagId)).willReturn(Optional.ofNullable(tag));

        //when
        underTest.updateTag(tagId, requestUpdate);

        //then
        ArgumentCaptor<Tag> tagArgumentCaptor = ArgumentCaptor.forClass(Tag.class);
        verify(tagRepository).save(tagArgumentCaptor.capture());

        Tag capturedTag = tagArgumentCaptor.getValue();
        assertThat(capturedTag).isEqualToComparingOnlyGivenFields(updatedCategory, "description", "name");
    }

    @Test
    public void canNotUpdateTagBecauseOfNotFound() {
        given(tagRepository.findById(any(Long.class))).willReturn(Optional.empty());

        assertThatThrownBy(() -> underTest.updateTag(1L, UpdateTagRequest.builder().build()))
                .isInstanceOf(TagNotFoundException.class)
                .hasMessageContaining("Tag not found with id: " + 1L);
    }

    @Test
    public void canDeleteTag() {
        //given
        Long tagId = 1L;
        given(tagRepository.findById(tagId)).willReturn(Optional.ofNullable(tag));

        //when
        underTest.deleteTag(1L);

        //then
        verify(tagRepository).deleteById(1L);
    }

    @AfterEach
    void tearDown() {
    }
}