package com.social.sarafan.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.social.sarafan.domain.User;
import com.social.sarafan.domain.Views;
import com.social.sarafan.dto.MessagePageDTO;
import com.social.sarafan.repository.UserDetailsRepository;
import com.social.sarafan.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.HashMap;

import static com.social.sarafan.controller.MessageController.MESSAGES_PER_PAGE;

@Controller
@RequestMapping("/")
public class MainController {

    private final MessageService messageService;
    private final ObjectWriter messageWriter;
    private final ObjectWriter profileWriter;
    private final UserDetailsRepository userDetailsRepository;

    @Autowired
    public MainController(MessageService messageService, ObjectMapper mapper, UserDetailsRepository userDetailsRepository) {
        this.messageService = messageService;
        this.userDetailsRepository = userDetailsRepository;

        ObjectMapper objectMapper = mapper
                .setConfig(mapper.getSerializationConfig());

        this.messageWriter = objectMapper
                .writerWithView(Views.FullMessage.class);
        this.profileWriter = objectMapper
                .writerWithView(Views.FullProfile.class);
    }

    @Value("${spring.profiles.active}")
    private String profile;

    @GetMapping
    public String main (
            Model model,
            @AuthenticationPrincipal User user
    ) throws JsonProcessingException {

        HashMap<Object, Object> data = new HashMap<>();

        if (user != null) {
            User userFromDB = userDetailsRepository.findById(user.getId()).get();
            String serializedProfile = profileWriter.writeValueAsString(userFromDB);
            model.addAttribute("profile", serializedProfile);

            Sort sort = Sort.by(Sort.Direction.DESC, "id");
            PageRequest pageRequest = PageRequest.of(0, MESSAGES_PER_PAGE, sort);
            MessagePageDTO messagePageDTO = messageService.findForUser(pageRequest, user);

            String messages = messageWriter.writeValueAsString(messagePageDTO.getMessages());   // send as string text, no object

            model.addAttribute("messages", messages);
            data.put("currentPage", messagePageDTO.getCurrentPage());
            data.put("totalPages", messagePageDTO.getTotalPages());
        } else {
            model.addAttribute("messages", "[]");
            model.addAttribute("profile", "null");
        }

        model.addAttribute("frontendData", data);
        model.addAttribute("isDevMode", "dev".equals(profile));
        return "index";
    }
}
