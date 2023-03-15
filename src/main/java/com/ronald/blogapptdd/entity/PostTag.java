package com.ronald.blogapptdd.entity;

import com.ronald.blogapptdd.entity.composite.PostTagKey;
import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "post_tag")
public class PostTag {
    @EmbeddedId
    private PostTagKey id;

    @ManyToOne
    @MapsId("postId")
    @JoinColumn(name = "post_id")
    private Post post;

    @ManyToOne
    @MapsId("tagId")
    @JoinColumn(name = "tag_id")
    private Tag tag;
}
