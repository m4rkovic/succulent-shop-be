package com.m4rkovic.succulent_shop.service;

import com.m4rkovic.succulent_shop.dto.CommentDTO;
import com.m4rkovic.succulent_shop.entity.Blog;
import com.m4rkovic.succulent_shop.entity.Comment;
import com.m4rkovic.succulent_shop.entity.User;
import com.m4rkovic.succulent_shop.exceptions.DeleteException;
import com.m4rkovic.succulent_shop.exceptions.InvalidDataException;
import com.m4rkovic.succulent_shop.exceptions.ResourceNotFoundException;
import com.m4rkovic.succulent_shop.exceptions.UnauthorizedException;
import com.m4rkovic.succulent_shop.mapper.CommentMapper;
import com.m4rkovic.succulent_shop.repository.CommentRepository;
import com.m4rkovic.succulent_shop.validator.CommentValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final CommentValidator commentValidator;
    private final CommentMapper commentMapper;
    private final BlogService blogService;
    private final UserService userService;

    @Override
    @Transactional(readOnly = true)
    public List<Comment> findAllByBlogId(Long blogId) {
        return commentRepository.findAllByBlogId(blogId);
    }

    @Override
    @Transactional(readOnly = true)
    public Comment findById(Long id) {
        return commentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("Comment not found with id: %d", id)));
    }

    @Override
    @Transactional
    public Comment save(CommentDTO commentDTO) {
        log.debug("Creating new comment for blog id: {}", commentDTO.getBlogId());
        commentValidator.validateAndThrow(commentDTO);

        Blog blog = blogService.findById(commentDTO.getBlogId());
        User currentUser = userService.getCurrentUser();

        Comment comment = commentMapper.toEntity(commentDTO);
        comment.setBlog(blog);
        comment.setAuthor(currentUser);
        comment.setApproved(true);

        try {
            Comment savedComment = commentRepository.save(comment);
            log.debug("Created comment with id: {}", savedComment.getId());
            return savedComment;
        } catch (DataIntegrityViolationException e) {
            throw new InvalidDataException("Failed to create comment due to data integrity violation", e);
        }
    }

    @Override
    @Transactional
    public Comment update(Long id, CommentDTO commentDTO) {
        log.debug("Updating comment with id: {}", id);
        commentValidator.validateAndThrow(commentDTO);

        Comment existingComment = findById(id);
        User currentUser = userService.getCurrentUser();

        // Only comment author can edit their comment
        if (!existingComment.getAuthor().getId().equals(currentUser.getId())) {
            throw new UnauthorizedException("You can only edit your own comments");
        }

        try {
            commentMapper.updateEntityFromDTO(existingComment, commentDTO);
            Comment updatedComment = commentRepository.save(existingComment);
            log.debug("Updated comment with id: {}", updatedComment.getId());
            return updatedComment;
        } catch (DataIntegrityViolationException e) {
            throw new InvalidDataException("Failed to update comment due to data integrity violation", e);
        }
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        log.debug("Deleting comment with id: {}", id);
        Comment comment = findById(id);

        User currentUser = userService.getCurrentUser();

        if (!comment.getAuthor().getId().equals(currentUser.getId()) &&
                !comment.getBlog().getAuthor().getId().equals(currentUser.getId())) {
            throw new UnauthorizedException("You don't have permission to delete this comment");
        }

        try {
            commentRepository.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            throw new DeleteException(
                    String.format("Cannot delete comment with id: %d due to existing references", id), e);
        }
    }

    @Override
    @Transactional
    public Comment approve(Long id) {
        log.debug("Approving comment with id: {}", id);
        Comment comment = findById(id);

        User currentUser = userService.getCurrentUser();
        // Only blog author or admin can approve comments
        if (!currentUser.getRole().equals("ADMIN") &&
                !comment.getBlog().getAuthor().getId().equals(currentUser.getId())) {
            throw new UnauthorizedException("You don't have permission to approve comments");
        }

        comment.setApproved(true);
        return commentRepository.save(comment);
    }

    @Override
    @Transactional
    public Comment addReply(Long parentId, CommentDTO replyDTO) {
        log.debug("Adding reply to comment id: {}", parentId);
        Comment parentComment = findById(parentId);

        // Set the blog ID from parent comment
        replyDTO.setBlogId(parentComment.getBlog().getId());
        replyDTO.setParentCommentId(parentId);

        return save(replyDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Comment> findPendingComments() {
        User currentUser = userService.getCurrentUser();
        if (currentUser.getRole().equals("ADMIN")) {
            return commentRepository.findByApprovedFalse();
        }
        return commentRepository.findByBlogAuthorIdAndApprovedFalse(currentUser.getId());
    }
}
