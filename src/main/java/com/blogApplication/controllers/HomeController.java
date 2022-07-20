package com.blogApplication.controllers;

import com.blogApplication.Comparators.SortInAscendingOrder;
import com.blogApplication.Comparators.SortInDescendingOrder;
import com.blogApplication.model.Comments;
import com.blogApplication.model.Posts;
import com.blogApplication.model.Tags;
import com.blogApplication.model.User;
import com.blogApplication.repository.PostsRepositry;
import com.blogApplication.repository.UserRepositry;
import com.blogApplication.service.CommentsService;
import com.blogApplication.service.PostsService;
import com.blogApplication.service.TagsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.support.PagedListHolder;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.*;

@Controller
public class HomeController {
    @Autowired
    private PostsService postsService;
    @Autowired
    private TagsService tagsService;
    @Autowired
    private PostsRepositry postsRepositry;
    @Autowired
    private UserRepositry userRepositry;
    @Autowired
    private SortInAscendingOrder sortInAscendingOrder;
    @Autowired
    private SortInDescendingOrder sortInDescendingOrder;
    private List<Posts> filteredPostsList=new ArrayList<>();
    private List<Integer> activeTagsList=new ArrayList<>();
    private List<Posts> searchInFilteredPostsList=new ArrayList<>();
    private List<Posts> authorRelatedPosts=new ArrayList<>();
    private List<Posts> allPosts=new ArrayList<>();
    private List<String> activeAuthorsList=new ArrayList<>();
    private String previousSearchInput;
    private String searchInMyPosts="";

    @RequestMapping("/")
    public String viewHomePage(Model model,Authentication auth,Principal principal){
        return findPaginated(1,model,"asc","",new ArrayList<>(),"allPosts",principal);
    }
    @GetMapping("/posts")
    public List<Posts> getData(){
        return postsService.getAllBlogPosts();
    }
    @RequestMapping("/clearAll")
    public String clearALlData(){
        filteredPostsList.clear();
        searchInFilteredPostsList.clear();
        authorRelatedPosts.clear();
        activeAuthorsList.clear();
        activeTagsList.clear();
        allPosts.clear();
        return "redirect:/";
    }
    @RequestMapping("/post/{id}")
    public String viewPost(@PathVariable Integer id, Model model,Principal principal){
        Optional<Posts> optionalPosts=postsService.findPostById(id);
        Posts postData=optionalPosts.get();
        List<Comments> commentsList=postData.getComments();
        String postAuthor=postData.getAuthor();
        if(principal!=null){
            String logedUserName=principal.getName();
            String role = "";
            User user=userRepositry.findUserByName(logedUserName);
            if(user!=null){
                role=user.getRole();
            }
            if(Objects.equals(logedUserName, postAuthor) || Objects.equals(role, "admin")){
                model.addAttribute("isUserPost",true);
            }else{
                model.addAttribute("isUserPost",false);
            }
            model.addAttribute("isLoggedIn",true);
        }else{
            model.addAttribute("isLoggedIn",false);
        }
        model.addAttribute("commentsList",commentsList);
        model.addAttribute("postData",postData);
        return "viewpostPage";
    }
    @GetMapping("/page/{pageNumber}/filters")
    public String getFilteredPosts(@PathVariable(value = "pageNumber") int pageNumber, Model model,
                                   @RequestParam(value = "sortType",defaultValue = "asc") String sortType,
                                   @RequestParam(value = "search",defaultValue = "") String search,
                                   @RequestParam(value = "authors",defaultValue = "")List<String> authorsList,
                                   @RequestParam(value = "tagId",defaultValue = "")List<Integer> tagIds){
        filteredPostsList.clear();
        authorRelatedPosts.clear();
        allPosts.clear();
        search=search.toLowerCase();
        if(!tagIds.isEmpty()){
            activeTagsList.addAll(tagIds);
        }
        if(!authorsList.isEmpty()){
            activeAuthorsList.addAll(authorsList);
        }
        if(!Objects.equals(previousSearchInput, "")){
            search=previousSearchInput;
            previousSearchInput="";
        }
        List<String> authorNamesList=new ArrayList<>();
        List<Posts> allPostsData=postsService.getAllBlogPosts();
        for (Posts post: allPostsData) {
            if(!authorNamesList.contains(post.getAuthor())){
                authorNamesList.add(post.getAuthor());
            }
        }
        PagedListHolder page=null;
        int pageSize=10;
        List<Posts> postsList = null;
        if(!activeAuthorsList.isEmpty()){
            authorRelatedPosts.clear();
            for (String authorName:activeAuthorsList) {
                List<Posts> authorPosts=postsRepositry.findAuthorPostsWithFilters(authorName,"");
                authorRelatedPosts.addAll(authorPosts);
            }
        }
        if(!activeTagsList.isEmpty()){
            if(!Objects.equals(search, "")){
                allPosts.clear();
            }
            filteredPostsList.clear();
            for (Integer id:activeTagsList){
                if(id!=0){
                    Optional<Tags> tagData=tagsService.findTagById(id);
                    Tags tagsData=tagData.get();
                    List<Posts> postsRelatedtags=tagsData.getPosts();
                    for (Posts post: postsRelatedtags) {
                        if(post.getTitle().toLowerCase().contains(search.toLowerCase())){
                            if(!filteredPostsList.contains(post)){
                                filteredPostsList.add(post);
                            }
                        }
                    }
                }
            }
        }
        if(!activeTagsList.isEmpty() && activeAuthorsList.isEmpty()){
            allPosts.addAll(filteredPostsList);
        } else if (activeTagsList.isEmpty() && !activeAuthorsList.isEmpty()) {
            allPosts.addAll(authorRelatedPosts);
        }else{
            allPosts.addAll(filteredPostsList);
            allPosts.retainAll(authorRelatedPosts);
        }
        if(sortType.equals("asc")){
            Collections.sort(allPosts, sortInAscendingOrder);
        }
        else if(sortType.equals("desc")){
            Collections.sort(allPosts,sortInDescendingOrder);
        }
        page=new PagedListHolder<>(allPosts);
        page.setPageSize(pageSize);
        page.setPage(pageNumber-1 );
        postsList=page.getPageList();
        List<Tags> allTagsList=tagsService.getAllTags();
        model.addAttribute("currentPage",pageNumber);
        model.addAttribute("tagsList",allTagsList);
        model.addAttribute("totalPages",page.getPageCount());
        model.addAttribute("blogsList",postsList);
        model.addAttribute("search",search);
        model.addAttribute("tagId",tagIds);
        model.addAttribute("authorsList",authorNamesList);
        model.addAttribute("sortType",sortType);
        model.addAttribute("activeAuthorsList",activeAuthorsList);
        model.addAttribute("activeTagsList",activeTagsList);
        return "viewFilteredPosts";
    }

    @GetMapping("/page/{pageNumber}")
    public String findPaginated(@PathVariable(value = "pageNumber") int pageNumber, Model model,
                                @RequestParam(value = "sortType",defaultValue = "asc") String sortType,
                                @RequestParam(value = "search",defaultValue = "") String search,
                                @RequestParam(value = "tagId",defaultValue = "")List<Integer> tagIds,
                                @RequestParam(value = "postsType",defaultValue = "allPosts") String selectedPostsType, Principal principal){
        int pageSize=10;
        List<String> authorNamesList=new ArrayList<>();
        String sortBy="published_at";
        Page<Posts> page;
        List<Posts> postsList = null;
        String userName="";
        previousSearchInput=search;
        activeTagsList.clear();
        search=search.toLowerCase();
        List<Posts> allPosts=postsService.getAllBlogPosts();
        for (Posts post: allPosts) {
            if(!authorNamesList.contains(post.getAuthor())){
                authorNamesList.add(post.getAuthor());
            }
        }
        if(principal!=null){
            model.addAttribute("isLoggedIn",true);
            model.addAttribute("userName",principal.getName());
            userName = principal.getName();
            model.addAttribute("userName",userName);
        }else{
            model.addAttribute("isLoggedIn",false);
            model.addAttribute("userName","");
        }
        if(principal==null && Objects.equals(selectedPostsType, "myPosts")){
            return "redirect:/loginPage";
        }
        page=postsService.findPaginated(pageNumber,pageSize,sortBy,sortType,search,selectedPostsType,userName);
        postsList=page.getContent();
        List<Tags> allTagsList=tagsService.getAllTags();
        model.addAttribute("currentPage",pageNumber);
        model.addAttribute("tagsList",allTagsList);
        model.addAttribute("authorsList",authorNamesList);
        model.addAttribute("totalPages",page.getTotalPages());
        model.addAttribute("totalItems",page.getTotalElements());
        model.addAttribute("blogsList",postsList);
        model.addAttribute("sortType",sortType);
        model.addAttribute("postsType",selectedPostsType);
        model.addAttribute("search",search);
        return "homePage";
    }
}
