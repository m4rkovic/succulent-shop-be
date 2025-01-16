package com.m4rkovic.succulent_shop.mapper;

import com.m4rkovic.succulent_shop.dto.CommentDTO;
import com.m4rkovic.succulent_shop.entity.Blog;
import com.m4rkovic.succulent_shop.entity.Comment;
import com.m4rkovic.succulent_shop.exceptions.ResourceNotFoundException;
import com.m4rkovic.succulent_shop.repository.CommentRepository;
import com.m4rkovic.succulent_shop.service.BlogService;
import com.m4rkovic.succulent_shop.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
@RequiredArgsConstructor
public class CommentMapper {
    private final BlogService blogService;
    private final UserService userService;

    private final CommentRepository commentRepository;

    public Comment toEntity(CommentDTO dto) {
        if (dto == null) {
            return null;
        }

        Comment comment = new Comment();

        comment.setId(dto.getId());
        comment.setContent(dto.getContent());
        comment.setApproved(dto.isApproved());
        comment.setLikeCount(dto.getLikeCount() != null ? dto.getLikeCount() : 0);
        comment.setCreatedAt(dto.getCreatedAt());
        comment.setUpdatedAt(dto.getUpdatedAt());

        if (dto.getBlogId() != null) {
            Blog blog = blogService.findById(dto.getBlogId());
            comment.setBlog(blog);
        }

        if (dto.getParentCommentId() != null) {
            Comment parentComment = findParentComment(dto.getParentCommentId());
            comment.setParentComment(parentComment);
        }

        return comment;
    }

    public CommentDTO toDTO(Comment entity) {
        if (entity == null) {
            return null;
        }

        return CommentDTO.builder()
                .id(entity.getId())
                .content(entity.getContent())
                .approved(entity.isApproved())
                .parentCommentId(entity.getParentComment() != null ? entity.getParentComment().getId() : null)
                .replies(entity.getReplies() != null ?
                        entity.getReplies().stream()
                                .map(this::toDTO)
                                .collect(Collectors.toList()) :
                        new ArrayList<>())
                .blogId(entity.getBlog() != null ? entity.getBlog().getId() : null)
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .likeCount(entity.getLikeCount())
                .build();
    }

    public void updateEntityFromDTO(Comment entity, CommentDTO dto) {
        if (entity == null || dto == null) {
            return;
        }

        if (StringUtils.isNotBlank(dto.getContent())) {
            entity.setContent(dto.getContent());
        }

        if (dto.getLikeCount() != null) {
            entity.setLikeCount(dto.getLikeCount());
        }

    }

    public List<CommentDTO> toDTOList(List<Comment> entities) {
        if (entities == null) {
            return Collections.emptyList();
        }
        return entities.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    private Comment findParentComment(Long parentId) {
        return commentRepository.findById(parentId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format("Parent comment not found with id: %d", parentId)
                ));
    }
}