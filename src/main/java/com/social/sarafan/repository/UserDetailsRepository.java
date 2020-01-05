package com.social.sarafan.repository;

import com.social.sarafan.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserDetailsRepository extends JpaRepository <User, String> {
}
