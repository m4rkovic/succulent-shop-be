package com.m4rkovic.succulent_shop.repository;

import com.m4rkovic.succulent_shop.entity.Blog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BlogRepository extends JpaRepository<Blog, Long> {
    Optional<Blog> findBySlug(String slug);

    @Query("SELECT b FROM Blog b WHERE b.published = true " +
            "AND (:query IS NULL OR " +
            "LOWER(b.title) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(b.content) LIKE LOWER(CONCAT('%', :query, '%'))) " +
            "AND (:tags IS NULL OR b.tags IN :tags)")
    Page<Blog> searchPublishedBlogs(
            @Param("query") String query,
            @Param("tags") List<String> tags,
            Pageable pageable
    );

    @Query("SELECT b FROM Blog b WHERE " +
            "(:query IS NULL OR " +
            "LOWER(b.title) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(b.content) LIKE LOWER(CONCAT('%', :query, '%'))) " +
            "AND (:tags IS NULL OR b.tags IN :tags)")
    Page<Blog> searchAllBlogs(
            @Param("query") String query,
            @Param("tags") List<String> tags,
            Pageable pageable
    );

    @Modifying
    @Query("UPDATE Blog b SET b.viewCount = b.viewCount + 1 WHERE b.id = :id")
    void incrementViewCount(@Param("id") Long id);
}
