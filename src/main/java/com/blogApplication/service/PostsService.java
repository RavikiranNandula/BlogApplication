package com.blogApplication.service;

import com.blogApplication.model.Posts;
import com.blogApplication.model.Tags;
import org.springframework.data.domain.Page;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface PostsService {
    void savePost(Posts post, Tags tag,String userName);
    void addComment(Posts post);
    void publishPost(Posts posts, Tags tag,String userName);
    void deletePost(Integer postId);
    List<Posts> getAllBlogPosts();
    Optional<Posts> findPostById(Integer id);
    void publishEditedPost(Posts post,Tags tag,Date createdDate,String userName);
    void saveEditedPost(Posts post, Tags tag,Date createdDate,String userName);
    Page<Posts> findPaginated(int pageNo,int pageSize,String sortBy,String sortType,String search,String selectedPosts,String userName);
    Page<Posts> findPostsForRestApi(int pageNo, int pageSize, String sortBy, String sortType, String search);
    Page<Posts> findFilteredPostsForRestApi(int pageNo, int pageSize, String sortBy, String sortType, String search, List<Integer> tagIds,List<String> authorsIds);
}
