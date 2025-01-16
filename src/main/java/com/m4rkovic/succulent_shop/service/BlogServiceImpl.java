package com.m4rkovic.succulent_shop.service;

import com.m4rkovic.succulent_shop.dto.BlogDTO;
import com.m4rkovic.succulent_shop.entity.Blog;
import com.m4rkovic.succulent_shop.entity.User;
import com.m4rkovic.succulent_shop.exceptions.*;
import com.m4rkovic.succulent_shop.mapper.BlogMapper;
import com.m4rkovic.succulent_shop.repository.BlogRepository;
import com.m4rkovic.succulent_shop.validator.BlogValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class BlogServiceImpl implements BlogService {
    private final BlogRepository blogRepository;
    private final BlogValidator blogValidator;
    private final BlogMapper blogMapper;
    private final FileStorageService fileStorageService;
    private final UserService userService;

    @Override
    @Transactional(readOnly = true)
    public List<Blog> findAll() {
        log.debug("Retrieving all blog posts");
        return blogRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Blog> findAllPaginated(Pageable pageable) {
        return blogRepository.findAll(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Blog findById(Long id) {
        log.debug("Retrieving blog with id: {}", id);
        return blogRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("Blog not found with id: %d", id)));
    }

    @Override
    @Transactional
    public Blog save(BlogDTO blogDTO) {
        log.debug("Creating new blog post");
        blogValidator.validateAndThrow(blogDTO);

        Blog blog = new Blog(); // Create new Blog instance instead of using mapper
        String oldPhotoUrl = blog.getPhotoUrl();

        try {
            // Set basic fields manually
            blog.setTitle(blogDTO.getTitle());
            blog.setSummary(blogDTO.getSummary());
            blog.setExcerpt(blogDTO.getExcerpt());
            blog.setContent(blogDTO.getContent());
            blog.setTags(blogDTO.getTags());
            blog.setPublished(blogDTO.isPublished());
            blog.setSlug(blogDTO.getSlug());

            User currentUser = userService.getCurrentUser();
            blog.setAuthor(currentUser);

            // Handle photo file exactly like in ProductServiceImpl
            if (blogDTO.getPhotoFile() != null && !blogDTO.getPhotoFile().isEmpty()) {
                try {
                    // Delete old photo if updating
                    if (oldPhotoUrl != null) {
                        fileStorageService.deleteFile(oldPhotoUrl);
                    }
                    String fileName = fileStorageService.storeFile(blogDTO.getPhotoFile());
                    blog.setPhotoUrl(fileName);
                    log.debug("Stored photo file with name: {}", fileName);
                } catch (Exception e) {
                    log.error("Failed to store photo file", e);
                    throw new FileStorageException("Failed to store photo file: " + e.getMessage());
                }
            }

            Blog savedBlog = blogRepository.save(blog);
            log.debug("Created blog with id: {} and photoUrl: {}", savedBlog.getId(), savedBlog.getPhotoUrl());
            return savedBlog;

        } catch (DataIntegrityViolationException e) {
            // If saving fails and we uploaded a photo, clean it up
            if (blog.getPhotoUrl() != null && !blog.getPhotoUrl().equals(oldPhotoUrl)) {
                try {
                    fileStorageService.deleteFile(blog.getPhotoUrl());
                } catch (Exception ex) {
                    log.error("Failed to cleanup photo after save failure", ex);
                }
            }
            throw new InvalidDataException("Failed to create blog post due to data integrity violation", e);
        } catch (Exception e) {
            // Clean up photo on any other error
            if (blog.getPhotoUrl() != null && !blog.getPhotoUrl().equals(oldPhotoUrl)) {
                try {
                    fileStorageService.deleteFile(blog.getPhotoUrl());
                    blog.setPhotoUrl(oldPhotoUrl); // Restore old photo URL
                } catch (Exception ex) {
                    log.error("Failed to cleanup photo after error", ex);
                }
            }
            throw e;
        }
    }
//    @Override
//    @Transactional
//    public Blog save(BlogDTO blogDTO) {
//        log.debug("Creating new blog post");
//        blogValidator.validateAndThrow(blogDTO);
//
//        Blog blog = blogMapper.toEntity(blogDTO);
//        String oldPhotoUrl = null;
//
//        try {
//            User currentUser = userService.getCurrentUser();
//            blog.setAuthor(currentUser);
//
//            // Handle photo file
//            if (blogDTO.getPhotoFile() != null && !blogDTO.getPhotoFile().isEmpty()) {
//                try {
//                    String fileName = fileStorageService.storeFile(blogDTO.getPhotoFile());
//                    blog.setPhotoUrl(fileName);
//                    log.debug("Stored photo file with name: {}", fileName);
//                } catch (Exception e) {
//                    log.error("Failed to store photo file", e);
//                    throw new FileStorageException("Failed to store photo file: " + e.getMessage());
//                }
//            }
//
//            Blog savedBlog = blogRepository.save(blog);
//            log.debug("Created blog with id: {} and photoUrl: {}", savedBlog.getId(), savedBlog.getPhotoUrl());
//            return savedBlog;
//
//        } catch (DataIntegrityViolationException e) {
//            // If saving fails and we uploaded a photo, clean it up
//            if (blog.getPhotoUrl() != null) {
//                try {
//                    fileStorageService.deleteFile(blog.getPhotoUrl());
//                } catch (Exception ex) {
//                    log.error("Failed to cleanup photo after save failure", ex);
//                }
//            }
//            throw new InvalidDataException("Failed to create blog post due to data integrity violation", e);
//        } catch (Exception e) {
//            // Clean up photo on any other error
//            if (blog.getPhotoUrl() != null) {
//                try {
//                    fileStorageService.deleteFile(blog.getPhotoUrl());
//                } catch (Exception ex) {
//                    log.error("Failed to cleanup photo after error", ex);
//                }
//            }
//            throw e;
//        }
//    }
//    @Override
//    @Transactional
//    public Blog save(BlogDTO blogDTO) {
//        log.debug("Creating new blog post");
//        blogValidator.validateAndThrow(blogDTO);
//
//        Blog blog = blogMapper.toEntity(blogDTO);
//        String photoUrl = null;
//
//        try {
//            User currentUser = userService.getCurrentUser();
//            blog.setAuthor(currentUser);
//
//            // Handle photo file
//            if (blogDTO.getPhotoFile() != null && !blogDTO.getPhotoFile().isEmpty()) {
//                try {
//                    String fileName = fileStorageService.storeFile(blogDTO.getPhotoFile());
//                    blog.setPhotoUrl(fileName);
//                    photoUrl = fileName; // Store the new photo URL
//                } catch (Exception e) {
//                    log.error("Failed to store photo file", e);
//                    throw new FileStorageException("Failed to store photo file: " + e.getMessage());
//                }
//            }
//
//            // Save the blog
//            Blog savedBlog = blogRepository.save(blog);
//            log.debug("Created blog with id: {}", savedBlog.getId());
//            return savedBlog;
//
//        } catch (DataIntegrityViolationException e) {
//            // If saving fails and we uploaded a photo, clean it up
//            if (photoUrl != null) {
//                try {
//                    fileStorageService.deleteFile(photoUrl);
//                } catch (Exception ex) {
//                    log.error("Failed to cleanup photo after save failure", ex);
//                }
//            }
//            throw new InvalidDataException("Failed to create blog post due to data integrity violation", e);
//        } catch (Exception e) {
//            // Clean up photo on any other error
//            if (photoUrl != null) {
//                try {
//                    fileStorageService.deleteFile(photoUrl);
//                } catch (Exception ex) {
//                    log.error("Failed to cleanup photo after error", ex);
//                }
//            }
//            throw e;
//        }
//    }

    @Override
    @Transactional
    public Blog update(Long id, BlogDTO blogDTO) {
        log.debug("Updating blog with id: {}", id);
        blogValidator.validateAndThrow(blogDTO);

        Blog existingBlog = findById(id);
        User currentUser = userService.getCurrentUser();

        if (!currentUser.getRole().equals("ADMIN") && !existingBlog.getAuthor().getId().equals(currentUser.getId())) {
            throw new UnauthorizedException("You can only edit your own blog posts");
        }

        String oldPhotoUrl = existingBlog.getPhotoUrl();

        try {
            blogMapper.updateEntityFromDTO(existingBlog, blogDTO);

            if (blogDTO.getPhotoFile() != null && !blogDTO.getPhotoFile().isEmpty()) {
                try {
                    if (oldPhotoUrl != null) {
                        fileStorageService.deleteFile(oldPhotoUrl);
                    }
                    String fileName = fileStorageService.storeFile(blogDTO.getPhotoFile());
                    existingBlog.setPhotoUrl(fileName);
                } catch (Exception e) {
                    log.error("Failed to store photo file", e);
                    throw new FileStorageException("Failed to store photo file: " + e.getMessage());
                }
            }

            Blog updatedBlog = blogRepository.save(existingBlog);
            log.debug("Updated blog with id: {}", updatedBlog.getId());
            return updatedBlog;

        } catch (DataIntegrityViolationException e) {
            throw new InvalidDataException("Failed to update blog post due to data integrity violation", e);
        } catch (Exception e) {
            if (blogDTO.getPhotoFile() != null && !blogDTO.getPhotoFile().isEmpty() && oldPhotoUrl != null) {
                try {
                    fileStorageService.deleteFile(existingBlog.getPhotoUrl());
                    existingBlog.setPhotoUrl(oldPhotoUrl);
                } catch (Exception ex) {
                    log.error("Failed to rollback photo changes", ex);
                }
            }
            throw e;
        }
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        log.debug("Deleting blog with id: {}", id);
        Blog blog = findById(id);

        User currentUser = userService.getCurrentUser();
        if (!currentUser.getRole().equals("ADMIN") && !blog.getAuthor().getId().equals(currentUser.getId())) {
            throw new UnauthorizedException("You can only delete your own blog posts");
        }

        try {
            if (blog.getPhotoUrl() != null) {
                try {
                    fileStorageService.deleteFile(blog.getPhotoUrl());
                } catch (Exception e) {
                    log.error("Failed to delete photo file for blog: {}", id, e);
                }
            }
            blogRepository.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            throw new DeleteException(
                    String.format("Cannot delete blog with id: %d due to existing references", id), e);
        }
    }

    @Override
    @Transactional
    public Blog updatePublishStatus(Long id, boolean published) {
        log.debug("Updating publish status for blog id: {} to: {}", id, published);
        Blog blog = findById(id);

        User currentUser = userService.getCurrentUser();
        if (!currentUser.getRole().equals("ADMIN") && !blog.getAuthor().getId().equals(currentUser.getId())) {
            throw new UnauthorizedException("You can only update publish status of your own blog posts");
        }

        blog.setPublished(published);
        return blogRepository.save(blog);
    }

    @Override
    @Transactional(readOnly = true)
    public Blog findBySlug(String slug) {
        return blogRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("Blog not found with slug: %s", slug)));
    }

    @Override
    @Transactional
    public void incrementViewCount(Long id) {
        blogRepository.incrementViewCount(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Blog> searchBlogs(String query, List<String> tags, boolean publishedOnly, Pageable pageable) {
        if (publishedOnly) {
            return blogRepository.searchPublishedBlogs(query, tags, pageable);
        }
        return blogRepository.searchAllBlogs(query, tags, pageable);
    }
}