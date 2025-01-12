package com.m4rkovic.succulent_shop.controller;

import com.m4rkovic.succulent_shop.dto.BlogDTO;
import com.m4rkovic.succulent_shop.entity.Blog;
import com.m4rkovic.succulent_shop.exceptions.InvalidDataException;
import com.m4rkovic.succulent_shop.response.BlogResponse;
import com.m4rkovic.succulent_shop.service.BlogService;
import com.m4rkovic.succulent_shop.service.FileStorageService;
import com.m4rkovic.succulent_shop.validator.BlogValidator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/v1/blogs")
@RequiredArgsConstructor
@Validated
@Tag(name = "Blog Controller", description = "Blog management APIs")
@CrossOrigin
@Slf4j
public class BlogApiController {
    private final BlogService blogService;
    private final BlogValidator blogValidator;
    private final FileStorageService fileStorageService;

    private static final Set<String> ALLOWED_IMAGE_TYPES = new HashSet<>(Arrays.asList(
            "image/jpeg",
            "image/jpg",
            "image/png",
            "image/gif"
    ));

    @Operation(summary = "Get a blog by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Blog found"),
            @ApiResponse(responseCode = "404", description = "Blog not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<BlogResponse> getBlog(
            @Parameter(description = "Blog ID", required = true)
            @PathVariable Long id) {
        Blog blog = blogService.findById(id);
        blogService.incrementViewCount(id);
        return ResponseEntity.ok(BlogResponse.fromEntity(blog));
    }


    @Operation(summary = "Get a blog by slug")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Blog found"),
            @ApiResponse(responseCode = "404", description = "Blog not found")
    })
    @GetMapping("/slug/{slug}")
    public ResponseEntity<BlogResponse> getBlogBySlug(
            @Parameter(description = "Blog slug", required = true)
            @PathVariable String slug) {
        Blog blog = blogService.findBySlug(slug);
        blogService.incrementViewCount(blog.getId());
        return ResponseEntity.ok(BlogResponse.fromEntity(blog));
    }

    @Operation(summary = "Get all blogs")
    @GetMapping
    public ResponseEntity<Page<BlogResponse>> getAllBlogs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDirection
    ) {
        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.Direction.fromString(sortDirection),
                sortBy
        );
        Page<Blog> blogPage = blogService.findAllPaginated(pageable);
        Page<BlogResponse> responsePage = blogPage.map(BlogResponse::fromEntity);
        return ResponseEntity.ok(responsePage);
    }

    @GetMapping("/photos/{fileName}")
    public ResponseEntity<Resource> getPhoto(@PathVariable String fileName) {
        try {
            Resource resource = fileStorageService.loadFileAsResource(fileName);
            String contentType = determineContentType(fileName);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_TYPE, contentType)
                    .header(HttpHeaders.CACHE_CONTROL, "max-age=31536000")
                    .body(resource);
        } catch (RuntimeException e) {
            log.error("Error loading photo: {}", fileName, e);
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Create a new blog post")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Blog created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<BlogResponse> createBlog(
            @RequestPart("blog") @Valid BlogDTO blogDto,
            @RequestPart(value = "photo", required = false) MultipartFile photoFile) {

        log.debug("Creating new blog with data: {}", blogDto);

        if (photoFile != null && !photoFile.isEmpty()) {
            validatePhoto(photoFile);
        }

        blogValidator.validateAndThrow(blogDto);
        Blog savedBlog = blogService.save(blogDto);

        BlogResponse response = BlogResponse.fromEntity(savedBlog);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(savedBlog.getId())
                .toUri();

        return ResponseEntity.created(location).body(response);
    }

    @Operation(summary = "Update a blog post")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Blog updated successfully"),
            @ApiResponse(responseCode = "404", description = "Blog not found"),
            @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<BlogResponse> updateBlog(
            @PathVariable Long id,
            @RequestPart("blog") @Valid BlogDTO blogDto,
            @RequestPart(value = "photo", required = false) MultipartFile photoFile) {

        log.debug("Updating blog {} with data: {}", id, blogDto);

        if (photoFile != null && !photoFile.isEmpty()) {
            validatePhoto(photoFile);
        }

        blogValidator.validateAndThrow(blogDto);
        Blog updatedBlog = blogService.update(id, blogDto);
        return ResponseEntity.ok(BlogResponse.fromEntity(updatedBlog));
    }

    @Operation(summary = "Update blog publish status")
    @PatchMapping("/{id}/publish")
    public ResponseEntity<BlogResponse> updatePublishStatus(
            @PathVariable Long id,
            @RequestParam boolean published) {
        Blog updatedBlog = blogService.updatePublishStatus(id, published);
        return ResponseEntity.ok(BlogResponse.fromEntity(updatedBlog));
    }

    @Operation(summary = "Delete a blog post")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Blog deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Blog not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBlog(
            @Parameter(description = "Blog ID", required = true)
            @PathVariable Long id) {
        log.debug("Deleting blog: {}", id);
        blogService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Search blogs")
    @GetMapping("/search")
    public ResponseEntity<Page<BlogResponse>> searchBlogs(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) List<String> tags,
            @RequestParam(required = false, defaultValue = "true") boolean publishedOnly,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDirection) {

        Pageable pageable = PageRequest.of(page, size,
                Sort.Direction.fromString(sortDirection), sortBy);
        Page<Blog> blogPage = blogService.searchBlogs(query, tags, publishedOnly, pageable);
        Page<BlogResponse> responsePage = blogPage.map(BlogResponse::fromEntity);
        return ResponseEntity.ok(responsePage);
    }

    private String determineContentType(String fileName) {
        try {
            String extension = fileName.substring(fileName.lastIndexOf(".")).toLowerCase();
            return switch (extension) {
                case ".jpg", ".jpeg" -> "image/jpeg";
                case ".png" -> "image/png";
                case ".gif" -> "image/gif";
                default -> "application/octet-stream";
            };
        } catch (Exception e) {
            log.warn("Could not determine content type for filename: {}", fileName);
            return "application/octet-stream";
        }
    }

    private void validatePhoto(MultipartFile file) {
        if (file.getSize() > 5 * 1024 * 1024) { // 5MB limit
            throw new InvalidDataException("File size exceeds 5MB limit");
        }

        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_IMAGE_TYPES.contains(contentType.toLowerCase())) {
            throw new InvalidDataException("Invalid file type. Only JPEG, PNG, and GIF are allowed");
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename != null && originalFilename.contains("..")) {
            throw new InvalidDataException("Invalid file name");
        }
    }
}