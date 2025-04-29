package com.navigram.server.controller;

import com.navigram.server.dto.CommentDto;
import com.navigram.server.service.CommentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/comments")
@CrossOrigin(origins = "*")
public class CommentController {
    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Map<String, Object>> createComment(@Valid @RequestBody CommentDto commentDto) {
        CommentDto createdComment = commentService.createComment(commentDto);
        
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Successfully created comment");
        response.put("data", createdComment);
        response.put("success", true);
        
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/memory/{memoryId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Map<String, Object>> getCommentsForMemory(@PathVariable String memoryId) {
        List<CommentDto> comments = commentService.getCommentsForMemory(memoryId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Successfully retrieved comments for memory");
        response.put("data", comments);
        response.put("success", true);
        
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('USER') and (authentication.principal.username == @commentService.getCommentById(#id).user.username or hasRole('ADMIN'))")
    public ResponseEntity<Map<String, Object>> deleteComment(@PathVariable String id) {
        commentService.deleteComment(id);
        
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Successfully deleted comment");
        response.put("success", true);
        
        return ResponseEntity.ok(response);
    }
}
