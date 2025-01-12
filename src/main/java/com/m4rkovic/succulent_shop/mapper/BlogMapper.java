package com.m4rkovic.succulent_shop.mapper;

import com.m4rkovic.succulent_shop.dto.BlogDTO;
import com.m4rkovic.succulent_shop.dto.CommentDTO;
import com.m4rkovic.succulent_shop.entity.Blog;
import com.m4rkovic.succulent_shop.entity.Comment;
import com.m4rkovic.succulent_shop.entity.User;
import com.m4rkovic.succulent_shop.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
@RequiredArgsConstructor
public class BlogMapper {
    private final UserService userService;

    public Blog toEntity(BlogDTO dto) {
        if (dto == null) {
            return null;
        }

        Blog blog = new Blog();

        // Set basic fields
        blog.setId(dto.getId());
        blog.setTitle(dto.getTitle());
        blog.setSummary(dto.getSummary());
        blog.setExcerpt(dto.getExcerpt());
        blog.setContent(dto.getContent());
        blog.setSlug(dto.getSlug());
        blog.setPhotoUrl(dto.getPhotoUrl());
        blog.setTags(dto.getTags() != null ? new ArrayList<>(dto.getTags()) : new ArrayList<>());
        blog.setPublished(dto.isPublished());
        blog.setViewCount(dto.getViewCount() != null ? dto.getViewCount() : 0L);
        blog.setCreatedAt(dto.getCreatedAt());
        blog.setUpdatedAt(dto.getUpdatedAt());
        blog.setMetadata(dto.getMetadata() != null ? new HashMap<>(dto.getMetadata()) : new HashMap<>());

        // Set author if authorId is provided (for admin updates)
        if (dto.getAuthorId() != null) {
            User author = userService.findById(dto.getAuthorId());
            blog.setAuthor(author);
        }

        return blog;
    }

    public BlogDTO toDTO(Blog entity) {
        if (entity == null) {
            return null;
        }

        return BlogDTO.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .summary(entity.getSummary())
                .excerpt(entity.getExcerpt())
                .content(entity.getContent())
                .authorId(entity.getAuthor() != null ? entity.getAuthor().getId() : null)
                .slug(entity.getSlug())
                .photoUrl(entity.getPhotoUrl())
                .tags(entity.getTags() != null ? new ArrayList<>(entity.getTags()) : new ArrayList<>())
                .published(entity.isPublished())
                .viewCount(entity.getViewCount())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .comments(convertCommentsToSummary(entity.getComments()))
                .metadata(entity.getMetadata() != null ? new HashMap<>(entity.getMetadata()) : new HashMap<>())
                .build();
    }

    private List<CommentDTO> convertCommentsToSummary(List<Comment> comments) {
        if (comments == null) {
            return new ArrayList<>();
        }
        return comments.stream()
                .map(this::convertToCommentSummary)
                .collect(Collectors.toList());
    }

    private CommentDTO convertToCommentSummary(Comment comment) {
        return CommentDTO.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .authorId(comment.getAuthor() != null ? comment.getAuthor().getId() : null)
                .createdAt(comment.getCreatedAt())
                .approved(comment.isApproved())
                .likeCount(comment.getLikeCount())
                .build();
    }

    public void updateEntityFromDTO(Blog entity, BlogDTO dto) {
        if (entity == null || dto == null) {
            return;
        }

        if (StringUtils.isNotBlank(dto.getTitle())) {
            entity.setTitle(dto.getTitle());
        }

        if (StringUtils.isNotBlank(dto.getSummary())) {
            entity.setSummary(dto.getSummary());
        }

        if (StringUtils.isNotBlank(dto.getExcerpt())) {
            entity.setExcerpt(dto.getExcerpt());
        }

        if (StringUtils.isNotBlank(dto.getContent())) {
            entity.setContent(dto.getContent());
        }

        if (StringUtils.isNotBlank(dto.getSlug())) {
            entity.setSlug(dto.getSlug());
        }

        if (StringUtils.isNotBlank(dto.getPhotoUrl())) {
            entity.setPhotoUrl(dto.getPhotoUrl());
        }

        if (dto.getTags() != null) {
            entity.setTags(new ArrayList<>(dto.getTags()));
        }

        entity.setPublished(dto.isPublished());

        if (dto.getMetadata() != null) {
            entity.setMetadata(new HashMap<>(dto.getMetadata()));
        }

        if (dto.getAuthorId() != null) {
            User author = userService.findById(dto.getAuthorId());
            entity.setAuthor(author);
        }
    }

    public List<BlogDTO> toDTOList(List<Blog> entities) {
        if (entities == null) {
            return Collections.emptyList();
        }
        return entities.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
}