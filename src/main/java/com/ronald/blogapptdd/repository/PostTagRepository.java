package com.ronald.blogapptdd.repository;

import com.ronald.blogapptdd.entity.PostTag;
import com.ronald.blogapptdd.entity.composite.PostTagKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostTagRepository extends JpaRepository<PostTag, PostTagKey> {
}
