package com.social.sarafan.service;

import com.social.sarafan.domain.Comment;
import com.social.sarafan.domain.User;
import com.social.sarafan.domain.Views;
import com.social.sarafan.dto.EventType;
import com.social.sarafan.dto.ObjectType;
import com.social.sarafan.repository.CommentRepository;
import com.social.sarafan.util.WsSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.function.BiConsumer;

@Service
public class CommentService {
    private final CommentRepository commentRepository;
    private final BiConsumer<EventType, Comment> wsSender;

    @Autowired
    public CommentService(CommentRepository commentRepository, WsSender wsSender) {
        this.commentRepository = commentRepository;
        this.wsSender = wsSender.getSender(ObjectType.COMMENT, Views.FullComment.class);
    }

    public Comment create(Comment comment, User user) {
        comment.setAuthor(user);
        Comment commentFromDB = commentRepository.save(comment);

        wsSender.accept(EventType.CREATE, commentFromDB);
        return commentFromDB;
    }

}
