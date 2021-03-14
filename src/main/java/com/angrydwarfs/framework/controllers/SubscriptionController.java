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
import com.angrydwarfs.framework.payload.response.MessageResponse;
import com.angrydwarfs.framework.repository.UserRepository;
import com.angrydwarfs.framework.service.SubscriptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

/**
 * Контроллер работы с подписками.
 * @version 0.001
 * @author habatoo
 */
@RestController
@RequestMapping("/api/auth/subscription")
public class SubscriptionController {
    private final SubscriptionService subscriptionService;
    private final UserRepository userRepository;


    @Autowired
    public SubscriptionController(SubscriptionService subscriptionService, UserRepository userRepository) {
        this.subscriptionService = subscriptionService;
        this.userRepository = userRepository;
    }

    /**
     * getUser - при http GET запросе по адресу .../api/auth/subscription/{id}
     * @return - пользователь
     * @param user
     */
    @GetMapping("{id}")
    @ResponseBody
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMINISTRATOR') or hasRole('ROLE_MODERATOR')")
    //@JsonView(Views.FullProfile.class)
    public User getUser(@PathVariable("id") User user) {
        return user;
    }

    /**
     * changeSubscription - при http POST запросе по адресу .../api/auth/change-subscription/{channelId}
     * @param authentication - пользователь кто подписывается
     * @param channel - пользователь на кого подписываются
     * @return - возвращает измененный статус подписки пользователя
     */
    @PostMapping("change-subscription/{channelId}")
    @ResponseBody
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMINISTRATOR') or hasRole('ROLE_MODERATOR')")
    //@JsonView(Views.FullProfile.class)
    public User changeSubscription(
            Authentication authentication,
            //@AuthenticationPrincipal User subscriber,
            @PathVariable("channelId") User channel
    ) {
        Optional optionalUser = userRepository.findByUsername(authentication.getName());
        if (optionalUser.isPresent()) {
            if (optionalUser.get().equals(channel)) {
                return channel;
            } else {
                return subscriptionService.changeSubscription(channel, (User)optionalUser.get());
            }
        } else {
            return channel;
        }
    }
}
