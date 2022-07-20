package com.blogApplication.controllers;

import com.blogApplication.model.Posts;
import com.blogApplication.model.Tags;
import com.blogApplication.model.User;
import com.blogApplication.repository.UserRepositry;
import com.blogApplication.service.CommentsService;
import com.blogApplication.service.PostsService;
import com.blogApplication.service.TagsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Controller
public class PostsController {
    @Autowired
    private PostsService postsService;
    @Autowired
    private UserRepositry userRepositry;
    @Autowired
    private TagsService tagsService;
    @Autowired
    private CommentsService commentsService;
    public Date createdDate;

    @GetMapping("/newpost")
    public String viewNewPostPage(Model model){
        Posts post =new Posts();
        Tags tag=new Tags();
        model.addAttribute("post",post);
        model.addAttribute("tag",tag);
        model.addAttribute("heading","New Post");
        model.addAttribute("buttonName","Save");
        return "newPostPage";
    }
    @PostMapping("/savePost")
    public String savePost(@ModelAttribute("post") Posts post,@ModelAttribute("tag") Tags tag,Principal principal){
        String userName=principal.getName();
        postsService.savePost(post,tag,userName);
        return "redirect:/";
    }
    @PostMapping("/publishPost")
    public String publishPost(@ModelAttribute("post") Posts post, @ModelAttribute("tag") Tags tag, Principal principal){
        System.out.println(tag.getName());
        String userName=principal.getName();
        postsService.publishPost(post,tag,userName);
        return "redirect:/";
    }
    @RequestMapping("/editpost/{id}")
    public String viewEditPostPage(@PathVariable int id, Model model,Principal principal){
        Optional<Posts> optionalPosts=postsService.findPostById(id);
        Posts post= optionalPosts.get();
        User user=null;
        if(principal!=null){
            user=userRepositry.findUserByName(principal.getName());
        }

        if(principal==null){
            return "access-denied";
        }else if(principal.getName().equals(post.getAuthor()) || Objects.equals(user.getRole(), "admin")){
            createdDate=post.getCreatedAt();
            List<Tags> tagsList=post.getTags();
            String tagsData="";
            for (Tags tag:tagsList) {
                tagsData=tagsData+tag.getName()+",";
            }
            model.addAttribute("tagsData",tagsData);
            model.addAttribute("post",post);
            model.addAttribute("buttonName","Update");
            model.addAttribute("heading","Edit Post");
            return "newPostPage";
        }
        return "access-denied";
    }
    @PostMapping("/editpost/updatePost")
    public String updatePost(@ModelAttribute("post") Posts post,@ModelAttribute("tag") Tags tag,Principal principal){
        String userName=principal.getName();
        postsService.saveEditedPost(post,tag,createdDate,userName);
        return "redirect:/";
    }
    @PostMapping("editpost/publishPost")
    public String publishUpdatedPost(@ModelAttribute("post") Posts post,@ModelAttribute("tag") Tags tag,Principal principal){
        String userName=principal.getName();
        postsService.publishEditedPost(post,tag,createdDate,userName);
        return "redirect:/";
    }
    @GetMapping("/delete/post/{id}")
    public String deletePost(@PathVariable("id") int postId,Principal principal){
        Optional<Posts> optionalPosts=postsService.findPostById(postId);
        Posts post=optionalPosts.get();
        User user=null;
        if(principal!=null){
            user=userRepositry.findUserByName(principal.getName());
        }
        if(principal==null){
            return "access-denied";
        }else if(principal.getName().equals(post.getAuthor()) || Objects.equals(user.getRole(), "admin")){
            commentsService.deleteAllCommentsRelatedToPost(post);
            postsService.deletePost(postId);
            return "redirect:/";
        }
        return "access-denied";
    }


}
