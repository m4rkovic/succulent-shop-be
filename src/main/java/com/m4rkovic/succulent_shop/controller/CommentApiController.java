package com.m4rkovic.succulent_shop.controller;

import com.m4rkovic.succulent_shop.dto.CommentDTO;
import com.m4rkovic.succulent_shop.entity.Comment;
import com.m4rkovic.succulent_shop.response.CommentResponse;
import com.m4rkovic.succulent_shop.service.CommentService;
import com.m4rkovic.succulent_shop.validator.CommentValidator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/comments")
@RequiredArgsConstructor
@Validated
@Tag(name = "Comment Controller", description = "Comment management APIs")
@CrossOrigin
@Slf4j
public class CommentApiController {
    private final CommentService commentService;
    private final CommentValidator commentValidator;

    @Operation(summary = "Get comments by blog ID")
    @GetMapping("/blog/{blogId}")
    public ResponseEntity<List<CommentResponse>> getCommentsByBlog(
            @PathVariable Long blogId) {
        List<Comment> comments = commentService.findAllByBlogId(blogId);
        List<CommentResponse> responses = comments.stream()
                .map(CommentResponse::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @Operation(summary = "Create a new comment")
    @PostMapping
    public ResponseEntity<CommentResponse> createComment(
            @RequestBody @Valid CommentDTO commentDto) {
        log.debug("Creating new comment for blog: {}", commentDto.getBlogId());

        commentValidator.validateAndThrow(commentDto);
        Comment savedComment = commentService.save(commentDto);

        CommentResponse response = CommentResponse.fromEntity(savedComment);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(savedComment.getId())
                .toUri();

        return ResponseEntity.created(location).body(response);
    }

    @Operation(summary = "Reply to a comment")
    @PostMapping("/{parentId}/reply")
    public ResponseEntity<CommentResponse> replyToComment(
            @PathVariable Long parentId,
            @RequestBody @Valid CommentDTO replyDto) {
        log.debug("Adding reply to comment: {}", parentId);

        Comment savedReply = commentService.addReply(parentId, replyDto);
        return ResponseEntity.ok(CommentResponse.fromEntity(savedReply));
    }

    @Operation(summary = "Update a comment")
    @PutMapping("/{id}")
    public ResponseEntity<CommentResponse> updateComment(
            @PathVariable Long id,
            @RequestBody @Valid CommentDTO commentDto) {
        log.debug("Updating comment: {}", id);

        commentValidator.validateAndThrow(commentDto);
        Comment updatedComment = commentService.update(id, commentDto);
        return ResponseEntity.ok(CommentResponse.fromEntity(updatedComment));
    }

    @Operation(summary = "Approve a comment")
    @PatchMapping("/{id}/approve")
    public ResponseEntity<CommentResponse> approveComment(@PathVariable Long id) {
        Comment approvedComment = commentService.approve(id);
        return ResponseEntity.ok(CommentResponse.fromEntity(approvedComment));
    }

    @Operation(summary = "Delete a comment")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long id) {
        log.debug("Deleting comment: {}", id);
        commentService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Get pending comments")
    @GetMapping("/pending")
    public ResponseEntity<List<CommentResponse>> getPendingComments() {
        List<Comment> pendingComments = commentService.findPendingComments();
        List<CommentResponse> responses = pendingComments.stream()
                .map(CommentResponse::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }
}
