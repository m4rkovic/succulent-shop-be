package com.m4rkovic.succulent_shop.service;

import com.m4rkovic.succulent_shop.dto.BlogDTO;
import com.m4rkovic.succulent_shop.entity.Blog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface BlogService {
    List<Blog> findAll();

    Page<Blog> findAllPaginated(Pageable pageable);

    Blog findById(Long id);

    Blog save(BlogDTO blogDTO);

    Blog update(Long id, BlogDTO blogDTO);

    void deleteById(Long id);

    Blog updatePublishStatus(Long id, boolean published);

    Blog findBySlug(String slug);

    void incrementViewCount(Long id);

    Page<Blog> searchBlogs(String query, List<String> tags, boolean publishedOnly, Pageable pageable);
}
