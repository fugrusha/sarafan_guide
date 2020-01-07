package com.social.sarafan.controller;

import com.fasterxml.jackson.annotation.JsonView;
import com.social.sarafan.domain.Message;
import com.social.sarafan.domain.Views;
import com.social.sarafan.dto.EventType;
import com.social.sarafan.dto.ObjectType;
import com.social.sarafan.repository.MessageRepository;
import com.social.sarafan.util.WsSender;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.function.BiConsumer;

@RestController
@RequestMapping("message")
public class MessageController {

    private final MessageRepository messageRepository;
    private final BiConsumer<EventType, Message> wsSender;

    @Autowired
    public MessageController(MessageRepository messageRepository, WsSender wsSender) {
        this.messageRepository = messageRepository;
        this.wsSender = wsSender.getSender(ObjectType.MESSAGE, Views.IdName.class);
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

        Message updatedMessage = messageRepository.save(message);

        wsSender.accept(EventType.CREATE, updatedMessage);

        return updatedMessage;
    }

    @PutMapping("/{id}")
    public Message updateMessage(
            @PathVariable("id") Message msgFromDB,   // get from DB via id
            @RequestBody Message message             // get from user's updated as json
    ) {
        // copy from message to msgFromDB all props except id
        BeanUtils.copyProperties(message, msgFromDB, "id");

        Message updatedMessage = messageRepository.save(message);

        wsSender.accept(EventType.UPDATE, updatedMessage);

        return messageRepository.save(msgFromDB);
    }

    @DeleteMapping("/{id}")
    public void deleteMessage(@PathVariable("id") Message message) {
        messageRepository.delete(message);
        wsSender.accept(EventType.REMOVE, message);
    }

}
