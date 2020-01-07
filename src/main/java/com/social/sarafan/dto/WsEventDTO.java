package com.social.sarafan.dto;

import com.fasterxml.jackson.annotation.JsonRawValue;
import com.fasterxml.jackson.annotation.JsonView;
import com.social.sarafan.domain.Views;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@JsonView(Views.Id.class)
public class WsEventDTO {
    private ObjectType objectType;
    private EventType eventType;
    @JsonRawValue
    private String body;
}
