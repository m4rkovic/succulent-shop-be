package com.m4rkovic.succulent_shop.repository;


import com.m4rkovic.succulent_shop.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findAllByBlogId(Long blogId);

    List<Comment> findByApprovedFalse();

    @Query("SELECT c FROM Comment c WHERE c.blog.author.id = :authorId AND c.approved = false")
    List<Comment> findByBlogAuthorIdAndApprovedFalse(@Param("authorId") Long authorId);
}
