/*
 * Copyright 2021 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.angrydwarfs.framework.service;

import com.angrydwarfs.framework.exceptions.EmailAlreadyExistsException;
import com.angrydwarfs.framework.exceptions.UsernameAlreadyExistsException;
import com.angrydwarfs.framework.models.MainRole;
import com.angrydwarfs.framework.models.UserPackage.User;
import com.angrydwarfs.framework.repository.UserRepository;
import com.angrydwarfs.framework.security.jwt.JwtUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class UserService {
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired
    private UserRepository userRepository;
    @Autowired private AuthenticationManager authenticationManager;
    @Autowired private JwtUtils jwtUtils;


    public String loginUser(String username, String password) {
        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(username, password));

        return jwtUtils.generateJwtToken(authentication);
    }

    public User registerUser(User user, MainRole role) {
        //log.info("registering user {}", user.getUsername());

        if(userRepository.existsByUsername(user.getUsername())) {
            //log.warn("username {} already exists.", user.getUsername());

            throw new UsernameAlreadyExistsException(
                    String.format("username %s already exists", user.getUsername()));
        }

        if(userRepository.existsByUserEmail(user.getUserEmail())) {
            //log.warn("email {} already exists.", user.getUserEmail());

            throw new EmailAlreadyExistsException(
                    String.format("email %s already exists", user.getUserEmail()));
        }
        //user.setActivationEmailStatus(true);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setMainRoles(new HashSet<MainRole>() {{
            add(role);
        }});

        return userRepository.save(user);
    }

    public List<User> findAll() {
        //log.info("retrieving all users");
        return userRepository.findAll();
    }

    public Optional<User> findByUsername(String username) {
        //log.info("retrieving user {}", username);
        return userRepository.findByUsername(username);
    }

    public Optional<User> findById(Long id) {
        //log.info("retrieving user {}", id);
        return userRepository.findById(id);
    }
}
