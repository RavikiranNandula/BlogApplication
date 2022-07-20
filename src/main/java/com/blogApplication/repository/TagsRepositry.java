package com.blogApplication.repository;

import com.blogApplication.model.Tags;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TagsRepositry extends JpaRepository<Tags, Integer> {
    Tags findTagByName(String name);
    @Query(value = "select id from tags",nativeQuery = true)
    List<Integer> getAllTags();
}
