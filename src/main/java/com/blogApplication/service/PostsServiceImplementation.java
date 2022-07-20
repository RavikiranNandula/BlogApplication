package com.blogApplication.service;


import com.blogApplication.model.Comments;
import com.blogApplication.model.Posts;
import com.blogApplication.model.Tags;
import com.blogApplication.repository.PostsRepositry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class PostsServiceImplementation implements PostsService{
    @Autowired
    private PostsRepositry postsRepositry;
    @Autowired
    private TagsService tagsService;
    @Override
    public void savePost(Posts post, Tags tag,String userName) {
        String excerpt;
        String content=post.getContent();
        Date date = new Date();
        int length=content.length();
        if(length>150){
            excerpt=content.substring(0,150)+".........";
        }
        else{
            excerpt=content+"......";
        }
        post.setExcerpt(excerpt);
        post.setAuthor(userName);
        post.setPublishedAt(null);
        post.setIsPublished(false);
        post.setCreatedAt(date);
        post.setUpdatedAt(null);

        String[] tagslist=tag.getName().split(",");
        for (String tagName:tagslist) {
            Tags newTag=new Tags();
            Tags tagData=tagsService.getTags(tagName.toLowerCase());
            tag.getPosts().add(post);
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
    }

    @Override
    public void addComment(Posts post) {
        postsRepositry.save(post);
    }

    @Override
    public void publishPost(Posts post, Tags tag,String userName) {
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
        post.setExcerpt(excerpt);
        post.setAuthor(userName);
        post.setPublishedAt(date);
        post.setIsPublished(true);
        post.setCreatedAt(date);
        post.setUpdatedAt(null);

        String[] tagslist=tag.getName().split(",");
        for (String tagName:tagslist) {
            Tags newTag=new Tags();
            Tags tagData=tagsService.getTags(tagName.toLowerCase());
            tag.getPosts().add(post);
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
    }

    @Override
    public void deletePost(Integer postId) {
        postsRepositry.deleteById(postId);
    }

    @Override
    public List<Posts> getAllBlogPosts() {
        List<Posts> allBlogPostsList=postsRepositry.findAll();
        return allBlogPostsList;
    }
    @Override
    public Optional<Posts> findPostById(Integer id) {
        return postsRepositry.findById(id);
    }

    public void publishEditedPost(Posts post,Tags tag,Date createdDate,String userName) {
        int id=post.getId();
        Optional<Posts> optionalPosts=postsRepositry.findById(id);
        Posts oldPostData=optionalPosts.get();
        List<Comments> oldPostComments=oldPostData.getComments();
        post.getComments().addAll(oldPostComments);
        String excerpt;
        String content=post.getContent();
        Date date = new Date();
        int length=content.length();
        if(length>150){
            excerpt=content.substring(0,150)+".........";
        }
        else{
            excerpt=content+"......";
        }
        post.setExcerpt(excerpt);
        post.setAuthor(oldPostData.getAuthor());
        post.setPublishedAt(date);
        post.setCreatedAt(createdDate);
        post.setIsPublished(true);
        post.setUpdatedAt(date);

        String[] tagslist=tag.getName().split(",");
        for (String tagName:tagslist) {
            Tags newTag=new Tags();
            Tags tagData=tagsService.getTags(tagName.toLowerCase());
            tag.getPosts().add(post);
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
    }

    @Override
    public void saveEditedPost(Posts post, Tags tag,Date createdDate,String userName) {
        int id=post.getId();
        Optional<Posts> oldPostOptionalData=postsRepositry.findById(id);
        Posts oldPost =oldPostOptionalData.get();
        List<Comments> oldPostComments=oldPost.getComments();
        post.getComments().addAll(oldPostComments);
        String title=post.getTitle();
        String excerpt;
        String content=post.getContent();
        Date date = new Date();
        int length=content.length();
        if(length>100){
            excerpt=content.substring(0,100)+".........";
        }
        else{
            excerpt=content+"......";
        }
        post.setTitle(title);
        post.setAuthor(oldPost.getAuthor());
        post.setCreatedAt(createdDate);
        post.setExcerpt(excerpt);
        post.setIsPublished(false);
        post.setContent(content);
        post.setUpdatedAt(date);

        String[] tagslist=tag.getName().split(",");
        for (String tagName:tagslist) {
            Tags newTag=new Tags();
            Tags tagData=tagsService.getTags(tagName.toLowerCase());
            tag.getPosts().add(post);
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
    }

    @Override
    public Page<Posts> findPaginated(int pageNo, int pageSize,String sortBy,String sortType,String search,String selectedPosts,String userName) {
        Sort sort=null;
        Pageable pageable=null;
        Page<Posts> posts=null;
        if(Objects.equals(sortType, "asc")){
            sort=Sort.by(sortBy).ascending();
            pageable= PageRequest.of(pageNo -1,pageSize,sort);
        }else{
            sort=Sort.by(sortBy).descending();
            pageable= PageRequest.of(pageNo -1,pageSize,sort);
        }
        if(Objects.equals(selectedPosts, "myPosts")){
            posts=postsRepositry.findAuthorPosts(pageable,userName,search);
        }else{
            posts=postsRepositry.findPostsBySearchInput(pageable,search);
        }
        return posts;
    }

    @Override
    public Page<Posts> findPostsForRestApi(int pageNo, int pageSize, String sortBy, String sortType, String search) {
        Sort sort=null;
        Pageable pageable=null;
        Page<Posts> posts=null;
        search=search.toLowerCase();
        if(Objects.equals(sortType, "asc")){
            sort=Sort.by(sortBy).ascending();
            pageable= PageRequest.of(pageNo -1,pageSize,sort);
        }else{
            sort=Sort.by(sortBy).descending();
            pageable= PageRequest.of(pageNo -1,pageSize,sort);
        }
        posts=postsRepositry.findPostsBySearchInput(pageable,search);
        return posts;
    }

    @Override
    public Page<Posts> findFilteredPostsForRestApi(int pageNo, int pageSize, String sortBy, String sortType, String search,List<Integer> tagIds,List<String> authorsIds) {
        Sort sort=null;
        Pageable pageable=null;
        Page<Posts> posts=null;
        search=search.toLowerCase();
        if(Objects.equals(sortType, "asc")){
            sort=Sort.by(sortBy).ascending();
            pageable= PageRequest.of(pageNo -1,pageSize,sort);
        }else{
            sort=Sort.by(sortBy).descending();
            pageable= PageRequest.of(pageNo -1,pageSize,sort);
        }
        posts=postsRepositry.findFilteredPostsWithRestApi(pageable,search,tagIds,authorsIds);
        return posts;
    }
}
