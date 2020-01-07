package com.social.sarafan.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.social.sarafan.dto.EventType;
import com.social.sarafan.dto.ObjectType;
import com.social.sarafan.dto.WsEventDTO;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.util.function.BiConsumer;

@Component
public class WsSender {
    private final SimpMessagingTemplate template;   // send messages via queue of messages
    private final ObjectMapper mapper;  // serialize and deserialize objects

    public WsSender(SimpMessagingTemplate template, ObjectMapper mapper) {
        this.template = template;
        this.mapper = mapper;
    }

    public <T> BiConsumer<EventType, T> getSender(ObjectType objectType, Class view) {
        ObjectWriter writer = mapper
                .setConfig(mapper.getSerializationConfig())
                .writerWithView(view);

        return (EventType eventType, T payload) -> {
            String value = null;

            try {
                value = writer.writeValueAsString(payload);
            } catch (JsonProcessingException e) {
                throw  new RuntimeException(e);
            }

            template.convertAndSend("/topic/activity",
                    new WsEventDTO(objectType, eventType, value)
            );
        };
    }
}
