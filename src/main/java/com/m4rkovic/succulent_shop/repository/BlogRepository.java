package com.m4rkovic.succulent_shop.repository;

import com.m4rkovic.succulent_shop.entity.Blog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BlogRepository extends JpaRepository<Blog, Long> {
    Optional<Blog> findBySlug(String slug);

    @Query("SELECT DISTINCT b FROM Blog b LEFT JOIN b.tags t " +
            "WHERE b.published = true " +
            "AND (:query IS NULL OR " +
            "LOWER(b.title) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(b.content) LIKE LOWER(CONCAT('%', :query, '%'))) " +
            "AND (COALESCE(:tags, NULL) IS NULL OR t IN :tags)")
    Page<Blog> searchPublishedBlogs(
            @Param("query") String query,
            @Param("tags") List<String> tags,
            Pageable pageable
    );

    @Query("SELECT DISTINCT b FROM Blog b LEFT JOIN b.tags t " +
            "WHERE (:query IS NULL OR " +
            "LOWER(b.title) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(b.content) LIKE LOWER(CONCAT('%', :query, '%'))) " +
            "AND (COALESCE(:tags, NULL) IS NULL OR t IN :tags)")
    Page<Blog> searchAllBlogs(
            @Param("query") String query,
            @Param("tags") List<String> tags,
            Pageable pageable
    );

    @Modifying
    @Query("UPDATE Blog b SET b.viewCount = b.viewCount + 1 WHERE b.id = :id")
    void incrementViewCount(@Param("id") Long id);
}