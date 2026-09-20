package com.supportticket.service;

import com.supportticket.api.dto.CommentResponse;
import com.supportticket.api.dto.CreateCommentRequest;

public interface CommentService {

    CommentResponse addComment(Long ticketId, CreateCommentRequest request);
}
