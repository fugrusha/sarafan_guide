package com.social.sarafan.dto;

import com.fasterxml.jackson.annotation.JsonView;
import com.social.sarafan.domain.Message;
import com.social.sarafan.domain.Views;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@JsonView(Views.FullMessage.class)
public class MessagePageDTO {
    private List<Message> messages;
    private int currentPage;
    private int totalPages;
}
