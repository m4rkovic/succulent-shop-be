package com.m4rkovic.succulent_shop.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Entity
@Table(name = "blogs")
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Blog extends AbstractEntity {

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, length = 200)
    private String summary;

    @Column(nullable = false, length = 500)
    private String excerpt;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User author;

    // For SEO purposes
    @Column(nullable = true, unique = true)
    private String slug;

    @Column(name = "photo_url", nullable = true)
    private String photoUrl;

    @ElementCollection
    private List<String> tags = new ArrayList<>();

    @Column(nullable = false)
    private boolean published = false;

    private Long viewCount = 0L;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "blog", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "blog_metadata")
    private Map<String, String> metadata = new HashMap<>();
}