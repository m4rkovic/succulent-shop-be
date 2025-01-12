package com.m4rkovic.succulent_shop.validator;

import com.m4rkovic.succulent_shop.dto.BlogDTO;
import com.m4rkovic.succulent_shop.exceptions.InvalidDataException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class BlogValidator {

    public List
            <String> validate(BlogDTO blogDTO) {
        List<String> violations = new ArrayList<>();

        validateBasicFields(blogDTO, violations);
        validateContent(blogDTO, violations);
        validateTags(blogDTO, violations);
        validateSlug(blogDTO, violations);
        validateAuthor(blogDTO, violations);

        return violations;
    }

    private void validateBasicFields(BlogDTO blogDTO, List<String> violations) {
        if (StringUtils.isBlank(blogDTO.getTitle())) {
            violations.add("Title cannot be empty");
        } else if (blogDTO.getTitle().length() < 2 || blogDTO.getTitle().length() > 100) {
            violations.add("Title must be between 2 and 100 characters");
        }

        if (StringUtils.isBlank(blogDTO.getSummary())) {
            violations.add("Summary cannot be empty");
        } else if (blogDTO.getSummary().length() > 200) {
            violations.add("Summary must not exceed 200 characters");
        }

        if (StringUtils.isBlank(blogDTO.getExcerpt())) {
            violations.add("Excerpt cannot be empty");
        } else if (blogDTO.getExcerpt().length() > 500) {
            violations.add("Excerpt must not exceed 500 characters");
        }
    }

    private void validateContent(BlogDTO blogDTO, List<String> violations) {
        if (StringUtils.isBlank(blogDTO.getContent())) {
            violations.add("Content cannot be empty");
        } else if (blogDTO.getContent().length() < 10) {
            violations.add("Content must be at least 10 characters long");
        }
    }

    private void validateTags(BlogDTO blogDTO, List<String> violations) {
        if (blogDTO.getTags() != null) {
            if (blogDTO.getTags().size() > 10) {
                violations.add("Maximum 10 tags allowed");
            }

            // Validate individual tags
            blogDTO.getTags().forEach(tag -> {
                if (StringUtils.isBlank(tag)) {
                    violations.add("Tags cannot be empty");
                } else if (tag.length() > 50) {
                    violations.add("Tag length cannot exceed 50 characters");
                }
            });
        }
    }

    private void validateSlug(BlogDTO blogDTO, List<String> violations) {
        if (StringUtils.isBlank(blogDTO.getSlug())) {
            violations.add("Slug cannot be empty");
        } else if (!blogDTO.getSlug().matches("^[a-z0-9-]+$")) {
            violations.add("Slug must contain only lowercase letters, numbers, and hyphens");
        }
    }

    private void validateAuthor(BlogDTO blogDTO, List<String> violations) {
        // Only validate authorId if it's provided (for updates)
        if (blogDTO.getId() != null && blogDTO.getAuthorId() == null) {
            violations.add("Author ID is required when updating a blog post");
        }
    }

    public void validateAndThrow(BlogDTO blogDTO) {
        List<String> violations = validate(blogDTO);
        if (!violations.isEmpty()) {
            throw new InvalidDataException(String.join(", ", violations));
        }
    }
}