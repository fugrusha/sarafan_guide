package com.social.sarafan.service;

import com.social.sarafan.domain.User;
import com.social.sarafan.repository.UserDetailsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class ProfileService {
    private final UserDetailsRepository userDetailsRepository;

    @Autowired
    public ProfileService(UserDetailsRepository userDetailsRepository) {
        this.userDetailsRepository = userDetailsRepository;
    }

    public User changeSubscription(User channel, User subscriber) {
        Set<User> subscribers = channel.getSubscribers();

        if (subscribers.contains(subscriber)) {
            subscribers.remove(subscriber);
        } else {
            subscribers.add(subscriber);
        }

        return userDetailsRepository.save(channel);
    }

    public User getProfile(User user) {
        return userDetailsRepository.findById(user.getId()).get();
    }
}
