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

package com.angrydwarfs.framework.controllers;

import com.angrydwarfs.framework.models.User;
import com.angrydwarfs.framework.models.Views;
import com.angrydwarfs.framework.payload.response.MessageResponse;
import com.angrydwarfs.framework.repository.ActivityRepository;
import com.angrydwarfs.framework.repository.MainRoleRepository;
import com.angrydwarfs.framework.repository.TokenRepository;
import com.angrydwarfs.framework.repository.UserRepository;
import com.angrydwarfs.framework.security.jwt.UserUtils;
import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * Контроллер работы с activity пользователя.
 * @version 0.001
 * @author habatoo
 */
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth/users/activities")
public class ActivityController {
    @Value("${dwarfsframework.app.remoteAddr}")
    private String remoteAddr;

    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final ActivityRepository activityRepository;

    @Autowired
    public ActivityController(
            UserRepository userRepository,
            TokenRepository tokenRepository,
            ActivityRepository activityRepository
    ) {
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
        this.activityRepository = activityRepository;
    }

    @Autowired
    MainRoleRepository roleRepository;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    UserUtils userUtils;

    /**
     * @method userActivityList - при http GET запросе по адресу .../api/auth/users/activity
     * @return {@code List<activities>} - список всех активностей пользователя с данными пользователя.
     * @see User
     */
    @GetMapping
    //@PreAuthorize("hasRole('ROLE_ADMINISTRATOR') or hasRole('ROLE_MODERATOR')")
    @ResponseBody
    @JsonView(Views.UserShortData.class)
    public ResponseEntity<?> userActivityList(Authentication authentication) {

        Optional optionalUser = userRepository.findByUserName(authentication.getName());
        if(!optionalUser.isPresent()) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: can not find user data."));
        }

        return ResponseEntity.ok(activityRepository.findByUserActivities(userRepository.findByUserName(authentication.getName()).get()));
    }
}
