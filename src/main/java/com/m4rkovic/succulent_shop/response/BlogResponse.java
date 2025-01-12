package com.m4rkovic.succulent_shop.response;

import com.m4rkovic.succulent_shop.entity.Blog;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BlogResponse {
    private Long id;
    private String title;
    private String summary;
    private String excerpt;
    private String content;
    private UserResponse author;
    private String slug;
    private String photoUrl;
    private List<String> tags;
    private boolean published;
    private Long viewCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<CommentResponse> comments;
    private Map<String, String> metadata;

    public static BlogResponse fromEntity(Blog blog) {
        if (blog == null) {
            return null;
        }

        return BlogResponse.builder()
                .id(blog.getId())
                .title(blog.getTitle())
                .summary(blog.getSummary())
                .excerpt(blog.getExcerpt())
                .content(blog.getContent())
                .author(UserResponse.fromEntity(blog.getAuthor()))
                .slug(blog.getSlug())
                .photoUrl(blog.getPhotoUrl())
                .tags(blog.getTags() != null ? new ArrayList<>(blog.getTags()) : new ArrayList<>())
                .published(blog.isPublished())
                .viewCount(blog.getViewCount())
                .createdAt(blog.getCreatedAt())
                .updatedAt(blog.getUpdatedAt())
                .comments(blog.getComments() != null ?
                        blog.getComments().stream()
                                .map(CommentResponse::fromEntity)
                                .collect(Collectors.toList()) :
                        new ArrayList<>())
                .metadata(blog.getMetadata() != null ?
                        new HashMap<>(blog.getMetadata()) :
                        new HashMap<>())
                .build();
    }
}