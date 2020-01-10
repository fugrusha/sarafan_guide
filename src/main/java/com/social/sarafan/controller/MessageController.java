package com.social.sarafan.controller;

import com.fasterxml.jackson.annotation.JsonView;
import com.social.sarafan.domain.Message;
import com.social.sarafan.domain.User;
import com.social.sarafan.domain.Views;
import com.social.sarafan.dto.EventType;
import com.social.sarafan.dto.MetaDTO;
import com.social.sarafan.dto.ObjectType;
import com.social.sarafan.repository.MessageRepository;
import com.social.sarafan.util.WsSender;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
@RequestMapping("message")
public class MessageController {

    private static String URL_PATTERN = "https?:\\/\\/?[\\w\\d\\._\\-%\\/\\?=&#]+";
    private static String IMAGE_PATTERN = "\\.(jpeg|jpg|gif|png)$";

    private static Pattern URL_REGEX = Pattern.compile(URL_PATTERN, Pattern.CASE_INSENSITIVE);
    private static Pattern IMG_REGEX = Pattern.compile(IMAGE_PATTERN, Pattern.CASE_INSENSITIVE);

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
    public Message createMessage(
            @RequestBody Message message,
            @AuthenticationPrincipal User user
    ) throws IOException {
        message.setCreationDate(LocalDateTime.now());
        fillMeta(message);
        message.setAuthor(user);

        Message updatedMessage = messageRepository.save(message);

        wsSender.accept(EventType.CREATE, updatedMessage);

        return updatedMessage;
    }

    @PutMapping("/{id}")
    public Message updateMessage(
            @PathVariable("id") Message msgFromDB,   // get from DB via id
            @RequestBody Message message             // get from user's updated as json
    ) throws IOException {
        // copy from message to msgFromDB all props except id
        BeanUtils.copyProperties(message, msgFromDB, "id");
        fillMeta(msgFromDB);
        Message updatedMessage = messageRepository.save(message);

        wsSender.accept(EventType.UPDATE, updatedMessage);

        return messageRepository.save(msgFromDB);
    }

    @DeleteMapping("/{id}")
    public void deleteMessage(@PathVariable("id") Message message) {
        messageRepository.delete(message);
        wsSender.accept(EventType.REMOVE, message);
    }

    private void fillMeta(Message message) throws IOException {
        String text = message.getText();
        // find url in text
        Matcher matcher = URL_REGEX.matcher(text);

        if (matcher.find()) {
            // get url from text
            String url = text.substring(matcher.start(), matcher.end());
            // check if url is an image
            matcher = IMG_REGEX.matcher(url);
            //set link to message
            message.setLink(url);
            // if it is an image
            if (matcher.find()) {
                message.setLinkCover(url);
            } else if (!url.contains("youtu")) { // if its youtube video all fields will fill automatically
                MetaDTO meta = getMeta(url);

                message.setLinkCover(meta.getCover());
                message.setLinkTitle(meta.getTitle());
                message.setLinkDescription(meta.getDescription());
            }
        }
    }

    private MetaDTO getMeta(String url) throws IOException {
        Document doc = Jsoup.connect(url).get();

        Elements title = doc.select("meta[name$=title],meta[property$=title]");
        Elements description = doc.select("meta[name$=description],meta[property$=description]");
        Elements cover = doc.select("meta[name$=image],meta[property$=image]");

        return new MetaDTO(
                getContent(title.first()),
                getContent(description.first()),
                getContent(cover.first())
        );
    }

    private String getContent(Element element) {
        return element == null ? "" : element.attr("content");
    }

}
