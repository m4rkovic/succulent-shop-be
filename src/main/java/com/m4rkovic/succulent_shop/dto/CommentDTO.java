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
    private Long id;

    @NotBlank(message = "Comment content is required")
    @Size(min = 1, max = 1000, message = "Comment must be between 1 and 1000 characters")
    private String content;

    @NotBlank(message = "Author name is required")
    @Size(min = 2, max = 50, message = "Author name must be between 2 and 50 characters")
    private String authorName;

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