package com.blogApplication.repository;

import com.blogApplication.model.Comments;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentsRepositry extends JpaRepository<Comments,Integer> {
}
