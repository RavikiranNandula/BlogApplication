package com.blogApplication.service;


import com.blogApplication.model.Comments;
import com.blogApplication.model.Posts;
import com.blogApplication.repository.CommentsRepositry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class CommentsServiceImplementation implements CommentsService{
    @Autowired
    private CommentsRepositry commentsRepositry;
    @Override
    public void saveComment(Comments comments) {
        commentsRepositry.save(comments);
    }

    @Override
    public Optional<Comments> findComment(Integer id) {
        return commentsRepositry.findById(id);
    }

    @Override
    public void deleteComment(Integer id) {
        commentsRepositry.deleteById(id);
    }

    @Override
    public void deleteAllCommentsRelatedToPost(Posts post) {
        List<Comments> commentsList=post.getComments();
        for (Comments comment:commentsList) {
            commentsRepositry.deleteById(comment.getId());
        }
    }
}
