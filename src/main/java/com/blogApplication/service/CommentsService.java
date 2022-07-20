package com.blogApplication.service;

import com.blogApplication.model.Comments;
import com.blogApplication.model.Posts;

import java.util.List;
import java.util.Optional;

public interface CommentsService{
    void saveComment(Comments comments);
    Optional<Comments> findComment(Integer id);
    void deleteComment (Integer id);
    void deleteAllCommentsRelatedToPost(Posts post);
}
