package com.blogApplication.RestApi.Controllers;

import com.blogApplication.RestApi.ErrorHandling.PostNotFoundException;
import com.blogApplication.RestApi.ErrorHandling.SecurityException;
import com.blogApplication.model.Comments;
import com.blogApplication.model.Posts;
import com.blogApplication.model.Tags;
import com.blogApplication.model.User;
import com.blogApplication.repository.CommentsRepositry;
import com.blogApplication.repository.PostsRepositry;
import com.blogApplication.repository.UserRepositry;
import com.blogApplication.service.PostsService;
import com.blogApplication.service.TagsService;
import com.blogApplication.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.*;

@RestController
public class RestPostsController {
    @Autowired
    private PostsService postsService;
    @Autowired
    private TagsService tagsService;
    @Autowired
    private PostsRepositry postsRepositry;
    @Autowired
    private CommentsRepositry commentsRepositry;
    @Autowired
    private UserRepositry userRepositry;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private UserService userService;

    @GetMapping("/api/posts")
    public List<Posts> getAllPosts(
            @RequestParam(value = "pageNumber",required = false,defaultValue = "1") Integer page,
            @RequestParam(value = "size",required = false,defaultValue = "10") Integer size,
            @RequestParam(value = "sortType",defaultValue = "asc") String sortType,
            @RequestParam(value = "search",defaultValue = "") String search) {
        String sortBy="published_at";
        Page<Posts> postsPage= postsService.findPostsForRestApi(page,size,sortBy,sortType,search);
        List<Posts> posts=postsPage.getContent();
        return posts;
    }
    @GetMapping("/api/posts/filters")
    public List<Posts> getAllFilteredPosts(@RequestParam(value = "pageNumber",required = false,defaultValue = "1") Integer page,
                                           @RequestParam(value = "size",required = false,defaultValue = "10") Integer size,
                                           @RequestParam(value = "sortType",defaultValue = "asc") String sortType,
                                           @RequestParam(value = "search",defaultValue = "") String search,
                                           @RequestParam(value = "tagId",defaultValue = "")List<Integer> tagIds,
                                           @RequestParam(value = "author",defaultValue = "")List<String> authorsNamesList){
        String sortBy="published_at";
        if(tagIds.isEmpty()){
            tagIds.addAll(tagsService.getAllTagIds());
        }
        if(authorsNamesList.isEmpty()){
            authorsNamesList.addAll(userRepositry.getAllUsersNames());
        }
        Page<Posts> postsPage= postsService.findFilteredPostsForRestApi(page,size,sortBy,sortType,search,tagIds,authorsNamesList);
        List<Posts> posts=postsPage.getContent();
        return posts;
    }
    @GetMapping("/api/posts/{id}")
    public Posts viewPost(@PathVariable Integer id, Principal principal) {
        Optional<Posts> optionalPosts=postsService.findPostById(id);
        Posts post =null;
        if(optionalPosts.isPresent()){
            post = optionalPosts.get();
            return post;
        }
        else{
            throw new PostNotFoundException("Post Not Found with id = "+id);
        }
    }
    @PostMapping("/api/posts/")
    public ResponseEntity<Posts> savePost(@RequestBody Posts post, Principal principal){
        if(principal==null){
            throw new SecurityException("Login to continue UnAuthorized Request");
        }
        String content=post.getContent();
        String excerpt;
        Date date = new Date();
        int length=content.length()/2;
        if(length>150){
            excerpt=content.substring(0,150)+".........";
        }
        else{
            excerpt=content+"......";
        }
        if(post.getAuthor()==null){
            post.setAuthor(principal.getName());
        }
        post.setExcerpt(excerpt);
        post.setPublishedAt(date);
        post.setIsPublished(true);
        post.setCreatedAt(date);
        post.setUpdatedAt(null);

        String[] tagslist=post.getTagsData().split(",");
        for (String tagName:tagslist) {
            Tags newTag=new Tags();
            Tags tagData=tagsService.getTags(tagName.toLowerCase());
            if(tagData==null){
                newTag.setName(tagName.toLowerCase());
                newTag.setCreatedAt(date);
                newTag.setUpdatedAt(null);
                tagsService.saveTag(newTag);
                post.getTags().add(newTag);
            }
            else{
                post.getTags().add(tagData);
            }
        }
        postsRepositry.save(post);
        return new ResponseEntity<>(post, HttpStatus.ACCEPTED);
    }

    @PutMapping("/api/posts/{id}")
    public ResponseEntity<Posts> updatePost(@RequestBody Posts post,@PathVariable("id") int id,Principal principal){
        Optional<Posts> optionalPosts=postsRepositry.findById(id);
        Posts existingPostData= null;
        if(optionalPosts.isEmpty()){
            throw new PostNotFoundException("The post with id "+id+" is not present to update");
        }else{
            existingPostData=optionalPosts.get();
        }
        User user=null;
        if(principal!=null){
            user=userRepositry.findUserByName(principal.getName());
        }
        if(principal==null){
            throw new SecurityException("You are not logged in Please login to continue");
        }else if(principal.getName().equals(existingPostData.getAuthor()) || Objects.equals(user.getRole(), "admin")) {
            Date createdDate = existingPostData.getCreatedAt();
            System.out.println(createdDate);
            List<Comments> oldPostComments = existingPostData.getComments();
            post.getComments().addAll(oldPostComments);
            String excerpt;
            String content = post.getContent();
            Date date = new Date();
            System.out.println(date);
            int length = content.length();
            if (length > 150) {
                excerpt = content.substring(0, 150) + ".........";
            } else {
                excerpt = content + "......";
            }
            post.setId(existingPostData.getId());
            post.setExcerpt(excerpt);
            post.setAuthor(existingPostData.getAuthor());
            post.setPublishedAt(date);
            post.setCreatedAt(createdDate);
            post.setIsPublished(true);
            post.setUpdatedAt(date);

            String[] tagslist = post.getTagsData().split(",");
            for (String tagName : tagslist) {
                Tags newTag = new Tags();
                Tags tagData = tagsService.getTags(tagName.toLowerCase());
                if (tagData == null) {
                    newTag.setName(tagName.toLowerCase());
                    newTag.setCreatedAt(date);
                    newTag.setUpdatedAt(null);
                    tagsService.saveTag(newTag);
                    post.getTags().add(newTag);
                } else {
                    post.getTags().add(tagData);
                }
            }
            postsRepositry.save(post);
        }else{
            throw new SecurityException("You are not the author to edit");
        }
        return new ResponseEntity<>(post,HttpStatus.ACCEPTED);
    }

    @DeleteMapping("/api/posts/{id}")
    public ResponseEntity<String> deletePost(@PathVariable("id") int id,Principal principal){
        Optional<Posts> optionalPosts=postsService.findPostById(id);
        Posts post=null;
        if(optionalPosts.isEmpty()){
            throw new PostNotFoundException("The post with id "+id+" is not present to delete");
        }else{
            post=optionalPosts.get();
        }
        User user=null;
        if(principal!=null){
            user=userRepositry.findUserByName(principal.getName());
        }
        if(principal==null){
            throw new SecurityException("You are not logged in");
        }else if(principal.getName().equals(post.getAuthor()) || Objects.equals(user.getRole(), "admin")) {
            List<Comments> commentsList = post.getComments();
            for (Comments comment : commentsList) {
                commentsRepositry.deleteById(comment.getId());
            }
            postsService.deletePost(id);
            return new ResponseEntity<>("Deleted Successfully",HttpStatus.ACCEPTED);
        }else{
            throw new SecurityException("You are not the author to delete");
        }
    }

    @PostMapping("api/posts/user/")
    public ResponseEntity<User> registerUser(@RequestBody User user){
        User oldUser=userRepositry.findUserByName(user.getName());
        if(Objects.equals(user.getName(), null) || Objects.equals(user.getEmail(), null) || Objects.equals(user.getPassword(), null)){
            throw new SecurityException("All Credentials are Required(name,email,password)");
        }
        if(oldUser!=null){
            throw new SecurityException("User name already exists");
        }
        String password=user.getPassword();
        user.setPassword(passwordEncoder. encode(password));
        userService.saveUser(user);
        return new ResponseEntity<>(user,HttpStatus.ACCEPTED);
    }

    @GetMapping("/products/{code}")
    public ResponseEntity<Posts> findByCode(@PathVariable int code){
        Optional<Posts> posts=postsService.findPostById(code);
        if(posts.isPresent()){
            Posts post=posts.get();
            return new ResponseEntity<>(post,HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
