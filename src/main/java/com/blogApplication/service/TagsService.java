package com.blogApplication.service;

import com.blogApplication.model.Tags;

import java.util.List;
import java.util.Optional;

public interface TagsService {
    void saveTag(Tags tag);
    Tags getTags(String name);
    List<Tags> getAllTags();
    Optional<Tags> findTagById(Integer id);
    List<Integer> getAllTagIds();
}
