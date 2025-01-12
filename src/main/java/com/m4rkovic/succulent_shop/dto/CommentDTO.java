package com.m4rkovic.succulent_shop.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentDTO {
//    private Long id;
//    private String content;
//    private Long authorId;
//    private LocalDateTime createdAt;
//    private boolean approved;
//    private Integer likeCount;

    private Long id;

    @NotBlank(message = "Comment content is required")
    @Size(min = 1, max = 1000, message = "Comment must be between 1 and 1000 characters")
    private String content;

    private Long authorId;
    @Email(message = "Please provide a valid email address")
    private String authorEmail;

    private boolean approved = false;

    private Long parentCommentId;
    private List<CommentDTO> replies = new ArrayList<>();

    @NotNull(message = "Blog ID is required")
    private Long blogId;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer likeCount = 0;
}