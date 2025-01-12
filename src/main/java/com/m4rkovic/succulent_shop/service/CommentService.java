package com.m4rkovic.succulent_shop.service;

import com.m4rkovic.succulent_shop.dto.CommentDTO;
import com.m4rkovic.succulent_shop.entity.Comment;

import java.util.List;

public interface CommentService {
    List<Comment> findAllByBlogId(Long blogId);

    Comment findById(Long id);

    Comment save(CommentDTO commentDTO);

    Comment update(Long id, CommentDTO commentDTO);

    void deleteById(Long id);

    Comment approve(Long id);

    Comment addReply(Long parentId, CommentDTO replyDTO);

    List<Comment> findPendingComments();
}