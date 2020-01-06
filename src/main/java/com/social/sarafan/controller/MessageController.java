package com.social.sarafan.controller;

import com.fasterxml.jackson.annotation.JsonView;
import com.social.sarafan.domain.Message;
import com.social.sarafan.domain.Views;
import com.social.sarafan.repository.MessageRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("message")
public class MessageController {

    private final MessageRepository messageRepository;

    @Autowired
    public MessageController(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    @GetMapping
    @JsonView(Views.IdName.class)
    public List<Message> list() {
        return messageRepository.findAll();
    }

    @GetMapping("/{id}")
    public Message getMessage(@PathVariable("id") Message message) {
        return message;
    }

    @PostMapping
    public Message createMessage(@RequestBody Message message) {
        message.setCreationDate(LocalDateTime.now());
        return messageRepository.save(message);
    }

    @PutMapping("/{id}")
    public Message updateMessage(
            @PathVariable("id") Message msgFromDB,   // get from DB via id
            @RequestBody Message message             // get from user's updated as json
    ) {
        // copy from message to msgFromDB all props except id
        BeanUtils.copyProperties(message, msgFromDB, "id");

        return messageRepository.save(msgFromDB);
    }

    @DeleteMapping("/{id}")
    public void deleteMessage(@PathVariable("id") Message message) {
        messageRepository.delete(message);
    }


    @MessageMapping("/changeMessage")
    @SendTo("/topic/activity")
    public Message change(Message message) {
        return messageRepository.save(message);
    }


}
