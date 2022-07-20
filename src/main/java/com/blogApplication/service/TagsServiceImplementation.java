package com.blogApplication.service;

import com.blogApplication.model.Tags;
import com.blogApplication.repository.TagsRepositry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TagsServiceImplementation implements TagsService{
    @Autowired
    private TagsRepositry tagsRepositry;
    @Override
    public void saveTag(Tags tag) {
        tagsRepositry.save(tag);
    }
    @Override
    public Tags getTags(String tagName) {
        Tags tags=tagsRepositry.findTagByName(tagName);
        return tags;
    }
    @Override
    public List<Tags> getAllTags() {
        return tagsRepositry.findAll();
    }
    @Override
    public Optional<Tags> findTagById(Integer id) {
        return tagsRepositry.findById(id);
    }

    @Override
    public List<Integer> getAllTagIds() {
        return tagsRepositry.getAllTags();
    }
}
