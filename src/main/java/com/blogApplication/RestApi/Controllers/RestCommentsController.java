package com.blogApplication.RestApi.Controllers;

import com.blogApplication.RestApi.ErrorHandling.CommentNotFoundException;
import com.blogApplication.RestApi.ErrorHandling.PostNotFoundException;
import com.blogApplication.RestApi.ErrorHandling.SecurityException;
import com.blogApplication.model.Comments;
import com.blogApplication.model.Posts;
import com.blogApplication.model.User;
import com.blogApplication.repository.CommentsRepositry;
import com.blogApplication.repository.PostsRepositry;
import com.blogApplication.repository.UserRepositry;
import com.blogApplication.service.CommentsService;
import com.blogApplication.service.PostsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@RestController
public class RestCommentsController {
    @Autowired
    private PostsService postsService;
    @Autowired
    private CommentsService commentsService;
    @Autowired
    private CommentsRepositry commentsRepositry;
    @Autowired
    private PostsRepositry postsRepositry;
    @Autowired
    private UserRepositry userRepositry;
    @GetMapping("/api/posts/{postId}/comments/{commentId}")
    public Comments getComment(@PathVariable("postId") int postId, @PathVariable("commentId") int commentId,Principal principal){
        Optional<Posts> optionalPosts=postsRepositry.findById(postId);
        Posts existingPostData= null;
        if(optionalPosts.isEmpty()){
            throw new PostNotFoundException("The post with id "+postId+" is not present to update comment");
        }else{
            existingPostData=optionalPosts.get();
        }
        Optional<Comments> optionalComments = commentsService.findComment(commentId);
        Comments commentsData =null;
        if(optionalComments.isEmpty()){
            throw new CommentNotFoundException("The comment with id "+commentId+" is not Present");
        }else{
            commentsData = optionalComments.get();
        }
        return commentsData;
    }
    @PostMapping("/api/posts/{id}/comments/")
    public String addComment(@PathVariable("id") int id, @RequestBody Comments comment){
        Optional<Posts> optionalPosts=postsRepositry.findById(id);
        if(optionalPosts.isEmpty()){
            throw new PostNotFoundException("The post with id "+id+" is not present to add comment");
        }
        Date date=new Date();
        comment.setCreatedAt(date);
        comment.setPost_id(id);
        commentsService.saveComment(comment);
        return "Comment Added";
    }
    @PutMapping("/api/posts/{postId}/comments/{commentId}")
    public String updateComment(@PathVariable("postId") int postId, @PathVariable("commentId") int commentId, @RequestBody Comments comments, Principal principal){
        Optional<Posts> optionalPosts=postsRepositry.findById(postId);
        Posts existingPostData= null;
        if(optionalPosts.isEmpty()){
            throw new PostNotFoundException("The post with id "+postId+" is not present to update comment");
        }else{
            existingPostData=optionalPosts.get();
        }
        List<Comments> commentsList=existingPostData.getComments();
        Optional<Comments> optionalComments = commentsService.findComment(commentId);
        Comments commentsData =null;
        if(optionalComments.isEmpty()){
            throw new CommentNotFoundException("The comment with id "+commentId+" is not Present");
        }else{
            commentsData = optionalComments.get();
        }
        User user=null;
        if(principal!=null){
            user=userRepositry.findUserByName(principal.getName());
        }
        if(principal==null){
            throw new SecurityException("You are not logged in Please login to continue");
        }else if(principal.getName().equals(existingPostData.getAuthor()) || Objects.equals(user.getRole(), "admin")) {
            if(commentsList.contains(commentsData)){
                Date createdAt = optionalComments.get().getCreatedAt();
                commentsData.setName(comments.getName());
                commentsData.setEmail(comments.getEmail());
                commentsData.setText(comments.getText());
                commentsData.setCreatedAt(createdAt);
                commentsData.setUpdated_at(new Date());
                commentsData.setPost_id(postId);
                commentsService.saveComment(commentsData);
                return "Comment Updated";
            }
            else{
                throw new CommentNotFoundException("This comment is not related to this post");
            }
        }else{
            throw new SecurityException("You are not the author to edit");
        }
    }
    @DeleteMapping("/api/posts/{postId}/comments/{commentId}")
    public String deleteComment(@PathVariable("postId") int postId,@PathVariable("commentId") int commentId,Principal principal){
        Optional<Posts> optionalPosts=postsRepositry.findById(postId);
        Posts existingPostData= null;
        if(optionalPosts.isEmpty()){
            throw new PostNotFoundException("The post with id "+postId+" is not present to update to delete comment");
        }else{
            existingPostData=optionalPosts.get();
        }
        Comments commentsData=null;
        Optional<Comments> optionalComments = commentsService.findComment(commentId);
        if(optionalComments.isEmpty()){
            throw new CommentNotFoundException("The comment with id "+commentId+" is not Present");
        }else{
            commentsData = optionalComments.get();
        }
        User user=null;
        if(principal!=null){
            user=userRepositry.findUserByName(principal.getName());
        }
        if(principal==null){
            throw new SecurityException("You are not logged in Please login to continue");
        }else if(principal.getName().equals(existingPostData.getAuthor()) || Objects.equals(user.getRole(), "admin")) {
            if(commentsData.getPost_id()==postId){
                commentsService.deleteComment(commentId);
                return "Comment Deleted";
            }
            else{
                throw new CommentNotFoundException("This Comment is not related to this post to delete");
            }
        }else{
            throw new SecurityException("You are not the author to Delete");
        }
    }
}
