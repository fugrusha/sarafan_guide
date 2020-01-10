package com.social.sarafan.controller;

import com.fasterxml.jackson.annotation.JsonView;
import com.social.sarafan.domain.Message;
import com.social.sarafan.domain.User;
import com.social.sarafan.domain.Views;
import com.social.sarafan.dto.MessagePageDTO;
import com.social.sarafan.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("message")
public class MessageController {
    public static final int MESSAGES_PER_PAGE = 3;

    private final MessageService messageService;

    @Autowired
    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @GetMapping
    @JsonView(Views.FullMessage.class)
    public MessagePageDTO list(
            @PageableDefault(size = MESSAGES_PER_PAGE, sort = {"id"}, direction = Sort.Direction.DESC) Pageable pageable
            ) {
        return messageService.findAll(pageable);
    }

    @GetMapping("/{id}")
    public Message getMessage(@PathVariable("id") Message message) {
        return message;
    }

    @PostMapping
    public Message createMessage(
            @RequestBody Message message,
            @AuthenticationPrincipal User user
    ) throws IOException {
        return messageService.create(message, user);
    }

    @PutMapping("/{id}")
    public Message updateMessage(
            @PathVariable("id") Message msgFromDB,   // get from DB via id
            @RequestBody Message message             // get from user's updated as json
    ) throws IOException {
        return messageService.update(msgFromDB, message);
    }

    @DeleteMapping("/{id}")
    public void deleteMessage(@PathVariable("id") Message message) {
        messageService.delete(message);
    }

}
