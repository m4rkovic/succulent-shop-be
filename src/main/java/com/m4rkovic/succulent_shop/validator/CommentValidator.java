package com.m4rkovic.succulent_shop.validator;

import com.m4rkovic.succulent_shop.dto.CommentDTO;
import com.m4rkovic.succulent_shop.exceptions.InvalidDataException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.ArrayList;

@Component
@Slf4j
public class CommentValidator {

    public List<String> validate(CommentDTO commentDTO) {
        List<String> violations = new ArrayList<>();

        validateContent(commentDTO, violations);
        validateBlogReference(commentDTO, violations);
        validateParentComment(commentDTO, violations);

        return violations;
    }

    private void validateContent(CommentDTO commentDTO, List<String> violations) {
        if (StringUtils.isBlank(commentDTO.getContent())) {
            violations.add("Comment content cannot be empty");
        } else if (commentDTO.getContent().length() < 1 || commentDTO.getContent().length() > 1000) {
            violations.add("Comment must be between 1 and 1000 characters");
        }
    }

    private void validateBlogReference(CommentDTO commentDTO, List<String> violations) {
        if (commentDTO.getBlogId() == null) {
            violations.add("Blog ID is required");
        }
    }

    private void validateParentComment(CommentDTO commentDTO, List<String> violations) {
        // If this is a reply (has parent comment ID), validate it
        if (commentDTO.getParentCommentId() != null) {
            // Note: Additional checks could be done here in service layer
            // like verifying parent comment exists and belongs to the same blog
            if (commentDTO.getParentCommentId() <= 0) {
                violations.add("Invalid parent comment ID");
            }
        }
    }

    public void validateAndThrow(CommentDTO commentDTO) {
        List<String> violations = validate(commentDTO);
        if (!violations.isEmpty()) {
            throw new InvalidDataException(String.join(", ", violations));
        }
    }
}