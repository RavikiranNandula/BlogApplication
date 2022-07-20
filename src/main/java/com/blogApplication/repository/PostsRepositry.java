package com.blogApplication.repository;

import com.blogApplication.model.Posts;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostsRepositry extends JpaRepository<Posts,Integer> {
    @Query(value = "select * from posts where id in (select posts.id from posts inner join post_tags on posts.id=post_tags.post_id inner join tags on tags.id=post_tags.tag_id \n" +
            "where (lower(posts.title) like %?1%) or (lower(posts.author) like %?1%) or (lower(posts.content) like %?1%) or(tags.name like %?1%))",nativeQuery = true)
    Page<Posts> findPostsBySearchInput(Pageable pageable,String title);
    @Query(value = "select * from posts where id in (select posts.id from posts inner join post_tags on posts.id=post_tags.post_id inner join tags on tags.id=post_tags.tag_id where (posts.author=?1) and ((lower(posts.title) like %?2%) or (lower(posts.content) like %?2%) or(tags.name like %?2%)))",nativeQuery = true)
    Page<Posts> findAuthorPosts(Pageable pageable,String userName,String search);
    @Query(value = "select * from posts where id in (select posts.id from posts inner join post_tags on posts.id=post_tags.post_id inner join tags on tags.id=post_tags.tag_id where (posts.author=?1) and ((lower(posts.title) like %?2%) or (lower(posts.content) like %?2%) or(tags.name like %?2%)))",nativeQuery = true)
    List<Posts> findAuthorPostsWithFilters(String userName,String search);
    @Query(value = "select * from posts where id IN"
            + " (SELECT posts.id FROM posts"
            + " INNER JOIN post_tags ON posts.id=post_tags.post_id INNER JOIN tags ON tags.id = post_tags.tag_id"
            + " WHERE  (lower(title) like %:search% or lower(content) like %:search% or lower(author) like %:search%)"
            + " and tags.id In :tagIds and posts.author In :authorsIds)", nativeQuery = true)
    Page<Posts> findFilteredPostsWithRestApi(Pageable pageable,@Param("search") String search,@Param("tagIds") List<Integer> tagIds,@Param("authorsIds") List<String> authorsIds);
}
