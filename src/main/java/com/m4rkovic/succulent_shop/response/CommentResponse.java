package com.m4rkovic.succulent_shop.response;

import com.m4rkovic.succulent_shop.entity.Comment;
import com.m4rkovic.succulent_shop.entity.User;
import com.m4rkovic.succulent_shop.enumerator.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentResponse {
    private Long id;
    private String content;
    private UserResponse author;
    private boolean approved;
    private Long parentCommentId;
    private List<CommentResponse> replies;
    private Long blogId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer likeCount;

    public static CommentResponse fromEntity(Comment comment) {
        if (comment == null) {
            return null;
        }

        return CommentResponse.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .author(UserResponse.fromEntity(comment.getAuthor()))
                .approved(comment.isApproved())
                .parentCommentId(comment.getParentComment() != null ?
                        comment.getParentComment().getId() :
                        null)
                .replies(comment.getReplies() != null ?
                        comment.getReplies().stream()
                                .map(CommentResponse::fromEntity)
                                .collect(Collectors.toList()) :
                        new ArrayList<>())
                .blogId(comment.getBlog() != null ?
                        comment.getBlog().getId() :
                        null)
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt())
                .likeCount(comment.getLikeCount())
                .build();
    }
}
