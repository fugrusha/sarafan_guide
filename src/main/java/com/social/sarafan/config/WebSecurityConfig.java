package com.social.sarafan.config;

import com.social.sarafan.domain.User;
import com.social.sarafan.repository.UserDetailsRepository;
import org.springframework.boot.autoconfigure.security.oauth2.client.EnableOAuth2Sso;
import org.springframework.boot.autoconfigure.security.oauth2.resource.PrincipalExtractor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

import java.time.LocalDateTime;

@Configuration
@EnableWebSecurity
@EnableOAuth2Sso
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .mvcMatchers("/").permitAll()   // allow to see main page for unregistered
                .anyRequest().authenticated()
                .and()
                .csrf().disable();
    }

    // factory bean to be able to map users and messages
    @Bean
    public PrincipalExtractor principalExtractor(UserDetailsRepository userDetailsRepository) {
        return map -> {
            String id = (String) map.get("sub");    // check if user exists or create new one
            User user = userDetailsRepository.findById(id).orElseGet(() -> {
                User newUser = new User();

                newUser.setId(id);
                newUser.setName((String) map.get("name"));
                newUser.setEmail((String) map.get("email"));
                newUser.setGender((String) map.get("gender"));
                newUser.setLocale((String) map.get("locale"));
                newUser.setUserpic((String) map.get("picture"));

                return newUser;
            });

            user.setLastVisit(LocalDateTime.now());
            return userDetailsRepository.save(user);
        };
    }
}
