package com.social.sarafan.service;

import com.social.sarafan.domain.Message;
import com.social.sarafan.domain.User;
import com.social.sarafan.domain.UserSubscription;
import com.social.sarafan.domain.Views;
import com.social.sarafan.dto.EventType;
import com.social.sarafan.dto.MessagePageDTO;
import com.social.sarafan.dto.MetaDTO;
import com.social.sarafan.dto.ObjectType;
import com.social.sarafan.repository.MessageRepository;
import com.social.sarafan.repository.UserSubscriptionRepository;
import com.social.sarafan.util.WsSender;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class MessageService {

    private static String URL_PATTERN = "https?:\\/\\/?[\\w\\d\\._\\-%\\/\\?=&#]+";
    private static String IMAGE_PATTERN = "\\.(jpeg|jpg|gif|png)$";

    private static Pattern URL_REGEX = Pattern.compile(URL_PATTERN, Pattern.CASE_INSENSITIVE);
    private static Pattern IMG_REGEX = Pattern.compile(IMAGE_PATTERN, Pattern.CASE_INSENSITIVE);

    private final MessageRepository messageRepository;
    private final UserSubscriptionRepository userSubscriptionRepository;
    private final BiConsumer<EventType, Message> wsSender;

    @Autowired
    public MessageService(MessageRepository messageRepository,
                          UserSubscriptionRepository userSubscriptionRepository,
                          WsSender wsSender
    ) {
        this.messageRepository = messageRepository;
        this.userSubscriptionRepository = userSubscriptionRepository;
        this.wsSender = wsSender.getSender(ObjectType.MESSAGE, Views.FullMessage.class);
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

    public void delete(Message message) {
        messageRepository.delete(message);
        wsSender.accept(EventType.REMOVE, message);
    }

    public Message update(Message msgFromDB, Message message) throws IOException {
        // copy text from message to msgFromDB
        msgFromDB.setText(message.getText());
        fillMeta(msgFromDB);
        Message updatedMessage = messageRepository.save(msgFromDB);

        wsSender.accept(EventType.UPDATE, updatedMessage);

        return messageRepository.save(msgFromDB);
    }

    public Message create(Message message, User user) throws IOException {
        message.setCreationDate(LocalDateTime.now());
        fillMeta(message);
        message.setAuthor(user);

        Message updatedMessage = messageRepository.save(message);

        wsSender.accept(EventType.CREATE, updatedMessage);

        return updatedMessage;
    }

    public MessagePageDTO findForUser(Pageable pageable, User user) {
        List<User> channels = userSubscriptionRepository.findBySubscriber(user)
                .stream()
                .filter(UserSubscription::isActive)
                .map(UserSubscription::getChannel)
                .collect(Collectors.toList());

        channels.add(user);

        Page<Message> page = messageRepository.findByAuthorIn(channels, pageable);

        return new MessagePageDTO(
                page.getContent(),
                pageable.getPageNumber(),
                page.getTotalPages()
        );
    }
}
